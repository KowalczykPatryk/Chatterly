package chatapp.server.controller;

import chatapp.server.dto.LoginRequest;
import chatapp.server.dto.LoginResponse;
import chatapp.server.model.Tokens;
import chatapp.server.model.User;
import chatapp.server.config.DependencyBinder;
import chatapp.server.service.UserService;
import chatapp.server.auth.JwtUtil;
import chatapp.server.dto.RefreshRequest;
import chatapp.server.exceptions.TokenValidationException;
import chatapp.server.exceptions.TokenPersistenceException;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import org.junit.jupiter.api.*;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.Client;
import jakarta.inject.Inject;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Integration tests for UserController endpoints using JerseyTest + Grizzly.
 */
public class UserControllerTest extends JerseyTest {
    @Mock
    private UserService userService;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        super.setUp();
    }

    @Override
    protected Application configure() {

        ResourceConfig config = new ResourceConfig(UserController.class, JacksonFeature.class);
        config.property(TestProperties.CONTAINER_PORT, "0");

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(userService).to(UserService.class);
            }
        });

        return config;
    }

    @Test
    @DisplayName("Registering a brand‐new user returns 201 Created")
    public void testRegisterNewUser() {

        when(userService.register(any(User.class))).thenReturn(true);

        Response resp = target("users")
                .path("register")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(new User("eva", "pw", "publicKey"), MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.CREATED.getStatusCode(), resp.getStatus(),
                "Expected 201 Created");

        String body = resp.readEntity(String.class);
        assertTrue(body.contains("User registered successfully"),
                "Response body should indicate successful registration");
    }

    @Test
    @DisplayName("Registering an existing user returns 409 Conflict")
    public void testRegisterDuplicateUser() {
        User bob = new User("bob", "secret", "publicKey");

        // Stub as already exists
        when(userService.register(bob)).thenReturn(false);

        Response second = target("users")
                .path("register")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(bob));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), second.getStatus(),
                "Expected 409 Conflict for duplicate username");
    }

    @Test
    @DisplayName("Successful login returns 200 OK and a token")
    public void testLoginSuccess() {

        when(userService.login(eq("carol"), eq("pw"))).thenReturn(new Tokens("accessToken", "refreshToken"));

        LoginRequest loginReq = new LoginRequest("carol", "pw");

        Response loginResp = target("users")
                .path("login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(loginReq, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), loginResp.getStatus(),
                "Expected 200 OK on valid login");

        LoginResponse lr = loginResp.readEntity(LoginResponse.class);
        assertTrue(lr.isSuccess(), "LoginResponse.success should be true");
        assertNotNull(lr.getAccessToken(), "LoginResponse.token should not be null");
        assertNotNull(lr.getRefreshToken(), "LoginResponse.token should not be null");
    }

    @Test
    @DisplayName("Failed login returns 401 Unauthorized")
    public void testLoginFailure() {

        when(userService.login(eq("dave"), eq("wrong"))).thenReturn(null);

        LoginRequest loginReq = new LoginRequest("dave", "wrong");

        Response resp = target("users")
                .path("login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(loginReq, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), resp.getStatus(),
                "Expected 401 Unauthorized on invalid credentials");
    }

    @Test
    @DisplayName("Successfuly retrieved refreshToken.")
    public void testRefreshTokenSuccessful() throws TokenValidationException, TokenPersistenceException{
        when(userService.refreshTokens(any(String.class))).thenReturn(new Tokens("accessToken", "refreshToken"));

        // alternatively we could use other stubbing method
        //     doReturn(new Tokens("accessToken", "refreshToken"))
        //        .when(userService)
        //        .refreshTokens(any(String.class));

        RefreshRequest req = new RefreshRequest();
        req.setRefreshToken("refreshToken");

        Response resp = target("users")
                .path("refreshToken")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
    }
    @Test
    @DisplayName("Successfuly logout.")
    public void testLogout() throws TokenValidationException, TokenPersistenceException{

        RefreshRequest req = new RefreshRequest();
        req.setRefreshToken("refreshToken");

        Response resp = target("users")
                .path("logout")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
    }

}
