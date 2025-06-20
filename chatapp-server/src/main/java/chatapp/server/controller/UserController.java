package chatapp.server.controller;

import chatapp.server.model.User;
import chatapp.server.dto.LoginRequest;
import chatapp.server.dto.LoginResponse;
import chatapp.server.dto.RefreshRequest;
import chatapp.server.dto.Tokens;
import chatapp.server.service.UserService;
import chatapp.server.dto.RegisterRequest;
import chatapp.server.dto.RegisterResponse;
import chatapp.server.utils.BcryptPasswordHasher;

import chatapp.server.exceptions.TokenPersistenceException;
import chatapp.server.exceptions.TokenValidationException;

import java.util.Map;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

/**
 * Kontroler Jersey obsługujący operacje rejestracji i logowania użytkownika.
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {
    @Inject
    private UserService userService;

    /**
     * Endpoint rejestracji nowego użytkownika.
     * POST /api/users/register
     * Body: { "username": "...", "password": "...", "publicKey": "..." }
     */
    @POST
    @Path("/register")
    public Response register(RegisterRequest req) {
        String password = req.getPassword();
        String passwordHash = BcryptPasswordHasher.hashPassword(password);
        boolean created = userService.register(new User(req.getUsername(), passwordHash, req.getPublicKey()));
        if (!created) {
            // 409 Conflict: login już zajęty
            return Response.status(Response.Status.CONFLICT)
                    .entity(new RegisterResponse("Username already exists."))
                    .build();
        }
        // 201 Created: udało się zarejestrować
        return Response.status(Response.Status.CREATED)
                .entity(new RegisterResponse("User registered successfully."))
                .build();
    }

    /**
     * Endpoint logowania użytkownika.
     * POST /api/users/login
     * Body: { "username": "...", "password": "..." }
     * Zwraca obiekt, który zawiera accessToken oraz refreshToken (lub komunikat o błędzie).
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        Tokens tokens = userService.login(request.getUsername(), request.getPassword());
        if (tokens == null) {
            // 401 Unauthorized: nieprawidłowe dane
            LoginResponse resp = new LoginResponse(false, null, null, "Invalid credentials.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(resp)
                    .build();
        }
        // Zwracamy 2 tokeny użytkownikowi
        LoginResponse resp = new LoginResponse(true, tokens.getAccessToken(), tokens.getRefreshToken(), "Login successful.");
        return Response.ok(resp).build();
    }

    /**
     * Endpoint do aktualizacji accessToken oraz refreshToken jeśli accessToken wygasł.
     * POST /api/users/refreshToken
     * Body: {"refreshToken": ...}
     * Zwraca obiekt, który zawiera accessToken oraz refreshToken (lub komunikat o błędzie)
     */
    @POST
    @Path("/refreshToken")
    public Response refresh(RefreshRequest req) {
        try {
            Tokens tokens = userService.refreshTokens(req.getRefreshToken());
            return Response.ok(tokens).build();

        } catch (TokenValidationException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of(
                            "error", e.getCode(),
                            "message", e.getMessage()))
                    .build();

        } catch (TokenPersistenceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(
                            "error", e.getCode(),
                            "message", e.getMessage()))
                    .build();
        }
    }

    /**
     * Endpoint wylogowania użytkownika.
     * POST /api/users/logout
     * Body: { "refreshToken": "..." }
     */
    @POST
    @Path("/logout")
    public Response logout(RefreshRequest req) {
        try {
            userService.logout(req.getRefreshToken());
            return Response.ok(Map.of("message", "Logout successful.")).build();
        } catch (TokenValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "error", e.getCode(),
                            "message", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(
                            "error", "INTERNAL_ERROR",
                            "message", "Failed to logout."))
                    .build();
        }
    }

}
