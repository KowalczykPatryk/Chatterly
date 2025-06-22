package chatapp.server.service;

import chatapp.server.model.User;
import chatapp.server.auth.JwtUtil;
import chatapp.server.dao.RefreshTokenDao;
import chatapp.server.dao.UserDao;
import chatapp.server.dao.UserStatusDao;
import chatapp.server.model.Tokens;

import chatapp.server.exceptions.TokenPersistenceException;
import chatapp.server.exceptions.TokenValidationException;

import java.sql.*;
import java.util.List;

public class UserService {
    private final UserDao userDao = new UserDao();
    private final JwtUtil jwtUtil = new JwtUtil();
    private final RefreshTokenDao refreshDao = new RefreshTokenDao();
    private final UserStatusDao userStatusDao = new UserStatusDao();
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
    public Tokens login(String username, String password) {
        try {
            if (!userDao.validateCredentials(username, password)) {
                return null;
            }
            String accessToken = jwtUtil.generateAccessToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);

            refreshDao.saveToken(refreshToken, username, jwtUtil.getRefreshExpiry());
            userStatusDao.setUserOnline(userDao.getId(username));
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
    private boolean validateAccessToken(String token) {
        if(JwtUtil.isTokenUpToDate(token))
        {
            return true;
        }
        return false;
    }

    /**
     * Sprawdza, czy accessToken jest ważny.
     * Zwraca listę usernames.
     */
    public List<String> getUsernames(String searchTerm, String accessToken) throws TokenValidationException, SQLException {
        if (validateAccessToken(accessToken))
        {
            String username = jwtUtil.getUsernameFromToken(accessToken);
            return userDao.searchUsernames(searchTerm, username);
        }
        else {
            throw new TokenValidationException("invalid_token", "Access token was not accepted.");
        }
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

    /**
     * Logout logic.
     * @throws TokenValidationException if token is invalid or revoked
     * @throws TokenPersistenceException if DB errors occur
     */
    public void logout(String oldRefreshToken)
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
        try {
            refreshDao.revoke(oldRefreshToken);
            userStatusDao.setUserOffline(userDao.getId(username));
        } catch (SQLException e) {
            throw new TokenPersistenceException("db_error",
                    "Failed to persist new refresh token.", e);
        }
    }
}
