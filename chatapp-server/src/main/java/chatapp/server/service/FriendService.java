package chatapp.server.service;

import chatapp.server.dto.*;
import chatapp.server.dao.FriendDao;
import chatapp.server.auth.JwtUtil;
import chatapp.server.exceptions.*;
import chatapp.server.model.Friend;
import chatapp.server.dao.UserDao;
import java.sql.*;
import java.util.*;

public class FriendService
{
    private final FriendDao friendDao = new FriendDao();
    private final JwtUtil jwtUtil = new JwtUtil();
    private final UserDao userDao = new UserDao();

    public void sendFriendRequest(String usernameFrom, String usernameTo, String accessToken) throws TokenValidationException, SQLException
    {
        if(JwtUtil.isTokenUpToDate(accessToken))
        {
            if(JwtUtil.getUsernameFromToken(accessToken).equals(usernameFrom))
            {
                int userIdFrom = userDao.getId(usernameFrom);
                int userIdTo = userDao.getId(usernameTo);
                friendDao.saveFriendRequest(userIdFrom, userIdTo);
                return;
            }
            throw new TokenValidationException("invalid_token", "access token has not belong to this username");
        }
        throw new TokenValidationException("invalid_token", "access token has expired");
    }

    public List<String> listIncomingRequests(String username, String accessToken) throws TokenValidationException, SQLException
    {
        if(JwtUtil.isTokenUpToDate(accessToken))
        {
            if (JwtUtil.getUsernameFromToken(accessToken).equals(username))
            {
                int userId = userDao.getId(username);
                return friendDao.getPendingRequests(userId);
            }
            throw new TokenValidationException("invalid_token", "access token has not belong to this username");
        }
        throw new TokenValidationException("invalid_token", "access token has expired");
    }

    public void respondToRequest(String usernameFrom, String usernameTo, boolean responseStatus, String accessToken) throws TokenValidationException, SQLException
    {
        if(JwtUtil.isTokenUpToDate(accessToken))
        {
            if (JwtUtil.getUsernameFromToken(accessToken).equals(usernameFrom))
            {
                int userIdFrom = userDao.getId(usernameFrom);
                int userIdTo = userDao.getId(usernameTo);
                if (responseStatus)
                {
                    friendDao.saveAcceptFriendRequest(userIdFrom, userIdTo);
                }
                else
                {
                    friendDao.saveRejectFriendRequest(userIdFrom, userIdTo);
                }
                return;
            }
            throw new TokenValidationException("invalid_token", "access token has not belong to this username");
        }
        throw new TokenValidationException("invalid_token", "access token has expired");
    }
    public void removeFriend(String usernameFrom, String usernameTo, String accessToken) throws TokenValidationException, SQLException
    {
        if(JwtUtil.isTokenUpToDate(accessToken))
        {
            if (JwtUtil.getUsernameFromToken(accessToken).equals(usernameFrom))
            {
                int userIdFrom = userDao.getId(usernameFrom);
                int userIdTo = userDao.getId(usernameTo);
                friendDao.removeFriend(userIdFrom, userIdTo);
            }
            throw new TokenValidationException("invalid_token", "access token has not belong to this username");
        }
        throw new TokenValidationException("invalid_token", "access token has expired");
    }
    public List<Friend> listFriends(String username, String accessToken) throws TokenValidationException, SQLException
    {
        if(JwtUtil.isTokenUpToDate(accessToken))
        {
            if (JwtUtil.getUsernameFromToken(accessToken).equals(username))
            {
                int userId = userDao.getId(username);
                return friendDao.getFriends(userId);
            }
            throw new TokenValidationException("invalid_token", "access token has not belong to this username");
        }
        throw new TokenValidationException("invalid_token", "access token has expired");
    }
    public String getStatus(String usernameFrom, String usernameTo, String accessToken) throws TokenValidationException, SQLException
    {
        if(JwtUtil.isTokenUpToDate(accessToken))
        {
            if (JwtUtil.getUsernameFromToken(accessToken).equals(usernameTo))
            {
                int userIdFrom = userDao.getId(usernameFrom);
                int userIdTo = userDao.getId(usernameTo);
                return friendDao.getFriendshipStatus(userIdFrom, userIdTo);
            }
            throw new TokenValidationException("invalid_token", "access token has not belong to this username");
        }
        throw new TokenValidationException("invalid_token", "access token has expired");
    }

}