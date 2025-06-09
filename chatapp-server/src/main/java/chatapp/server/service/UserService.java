package chatapp.server.service;

import chatapp.server.model.User;
import chatapp.server.auth.JwtUtil;
import chatapp.server.dao.RefreshTokenDao;
import chatapp.server.dao.UserDao;
import chatapp.server.dto.Tokens;

import chatapp.server.exceptions.TokenPersistenceException;
import chatapp.server.exceptions.TokenValidationException;

import java.sql.*;

public class UserService {
    private final UserDao userDao = new UserDao();
    private final JwtUtil jwtUtil = new JwtUtil();
    private final RefreshTokenDao refreshDao = new RefreshTokenDao();
    /**
     * Próbuje zarejestrować nowego użytkownika.
     * Zwraca true jeśli użytkownik nie istniał, false jeśli login już zajęty.
     */
    public boolean register(User newUser) {
        try {
            if (userDao.userExists(newUser.getUsername())) {
                return false;
            }
            userDao.addUser(newUser);
            return true;
        } catch(SQLException e)
        {
            return false;
        }
    }

    /**
     * Próbuje zalogować użytkownika: jeśli login i hasło poprawne,
     * zwraca wygenerowany token (lub null, jeśli nieprawidłowe dane).
     */
    public Tokens login(String username, String passwordHash) {
        try {
            UserDao userDao = new UserDao();
            if (!userDao.validateCredentials(username, passwordHash)) {
                return null;
            }
            String accessToken = jwtUtil.generateAccessToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);

            refreshDao.saveToken(refreshToken, username, jwtUtil.getRefreshExpiry());
            return new Tokens(accessToken, refreshToken);
        } catch( SQLException e)
        {
            return null;
        }
    }

    /**
     * Sprawdza, czy token (przypisany do danego loginu) jest ważny.
     * Używane przy np. otwieraniu WebSocket
     */
    public boolean validateAccessToken(String username, String token) {
        if(JwtUtil.isTokenUpToDate(token))
        {
            if(JwtUtil.getUsernameFromToken(token) == username)
            {
                return true;
            }
        }
        return false;
    }
    /**
     * Validate old refresh token, rotate it, and return new Tokens.
     * @throws TokenValidationException if token is invalid or revoked
     * @throws TokenPersistenceException if DB errors occur
     */
    public Tokens refreshTokens(String oldRefreshToken)
            throws TokenValidationException, TokenPersistenceException {

        if (oldRefreshToken == null || oldRefreshToken.isBlank()) {
            throw new TokenValidationException("missing_token",
                    "Refresh token was not provided.");
        }
        if(jwtUtil.isTokenUpToDate(oldRefreshToken))
        {
            throw new TokenValidationException("invalid_token",
                    "Refresh token is illformed or expired.");
        }
        try {
            if (!refreshDao.isValid(oldRefreshToken)) {
                throw new TokenValidationException("revoked_token",
                        "Refresh token has been revoked.");
            }
        } catch (SQLException e) {
            throw new TokenPersistenceException("db_error",
                    "Unable to validate refresh token in database.", e);
        }
        String username = jwtUtil.getUsernameFromToken(oldRefreshToken);
        String newAccess  = jwtUtil.generateAccessToken(username);
        String newRefresh = jwtUtil.generateRefreshToken(username);

        try {
            refreshDao.revoke(oldRefreshToken);
            refreshDao.saveToken(newRefresh, username, jwtUtil.getRefreshExpiry());
        } catch (SQLException e) {
            throw new TokenPersistenceException("db_error",
                    "Failed to persist new refresh token.", e);
        }

        return new Tokens(newAccess, newRefresh);
    }
}
