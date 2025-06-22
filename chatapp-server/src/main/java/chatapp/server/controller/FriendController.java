package chatapp.server.controller;

import java.util.Map;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

import chatapp.server.service.FriendService;
import chatapp.server.service.UserService;
import chatapp.server.exceptions.*;
import chatapp.server.model.Friend;

import java.sql.*;
import java.util.List;

import chatapp.server.dto.*;

/**
 * Controller Jersey handling friend management operations.
 */
@Path("/friends")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FriendController {
    @Inject
    private FriendService friendService;

    /**
     * Send a new friend request.
     * POST /api/friends/requests
     * Header: Authorization: Bearer <token>
     * Body: FriendAddRequest { usernameFrom, usernameTo }
     */
    @POST
    @Path("/requests")
    public Response sendFriendRequest(@HeaderParam("Authorization") String authHeader,
                                      FriendAddRequest request) {
        String accessToken = authHeader.replaceFirst("(?i)Bearer\\s+", "");
        String usernameFrom = request.getUsernameFrom();
        String usernameTo = request.getUsernameTo();
        try {
            friendService.sendFriendRequest(usernameFrom, usernameTo, accessToken);
            return Response.status(Response.Status.CREATED).build();
        } catch (TokenValidationException e)
        {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        } catch (SQLException e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * List incoming friend requests.
     * GET /api/friends/requests/incoming/{username}
     * Header: Authorization: Bearer <token>
     */
    @GET
    @Path("/requests/incoming/{username}")
    public Response listIncomingRequests(@PathParam("username") String username,
                                         @HeaderParam("Authorization") String authHeader) {
        String accessToken = authHeader.replaceFirst("(?i)Bearer\\s+", "");
        try {
            List<String> usernames = friendService.listIncomingRequests(username, accessToken);
            return Response.ok(usernames).build();
        } catch (TokenValidationException e)
        {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        } catch (SQLException e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * Respond to a friend request (accept or reject).
     * POST /api/friends/requests/respond
     * Header: Authorization: Bearer <token>
     * Body: FriendRequestResponse { usernameFrom, usernameTo, responseStatus }
     */
    @POST
    @Path("/requests/respond")
    public Response respondToFriendRequest(@HeaderParam("Authorization") String authHeader,
                                           FriendRequestResponse response) {
        String accessToken = authHeader.replaceFirst("(?i)Bearer\\s+", "");
        String usernameFrom = response.getUsernameFrom();
        String usernameTo = response.getUsernameTo();
        boolean responseStatus = response.getStatus();
        try {
            friendService.respondToRequest(usernameFrom, usernameTo, responseStatus, accessToken);
            return Response.ok().build();
        } catch (TokenValidationException e)
        {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        } catch (SQLException e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * Remove (unfriend) a user.
     * DELETE /api/friends/{usernameFrom}/with/{usernameTo}
     * Header: Authorization: Bearer <token>
     */
    @DELETE
    @Path("/{usernameFrom}/with/{usernameTo}")
    public Response removeFriend(@PathParam("usernameFrom") String usernameFrom,
                                 @PathParam("usernameTo") String usernameTo,
                                 @HeaderParam("Authorization") String authHeader) {
        String accessToken = authHeader.replaceFirst("(?i)Bearer\\s+", "");
        try {
            friendService.removeFriend(usernameFrom, usernameTo, accessToken);
            return Response.ok().build();
        } catch (TokenValidationException e)
        {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        } catch (SQLException e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * List all friends of a user.
     * GET /api/friends/{username}/list
     * Header: Authorization: Bearer <token>
     */
    @GET
    @Path("/{username}/list")
    public Response listFriends(@PathParam("username") String username,
                                @HeaderParam("Authorization") String authHeader) {
        String accessToken = authHeader.replaceFirst("(?i)Bearer\\s+", "");
        try {
            List<Friend> friends = friendService.listFriends(username, accessToken);
            return Response.ok(friends).build();
        } catch (TokenValidationException e)
        {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        } catch (SQLException e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    /**
     * Get friendship status between two users.
     * GET /api/friends/{usernameFrom}/status/{usernameTo}
     * Header: Authorization: Bearer <token>
     */
    @GET
    @Path("/{usernameFrom}/status/{usernameFriend}")
    public Response getFriendshipStatus(@PathParam("usernameFrom") String usernameFrom,
                                        @PathParam("usernameFriend") String usernameTo,
                                        @HeaderParam("Authorization") String authHeader) {
        String accessToken = authHeader.replaceFirst("(?i)Bearer\\s+", "");
        try {
            String status = friendService.getStatus(usernameFrom, usernameTo, accessToken);
            return Response.ok(status).build();
        } catch (TokenValidationException e)
        {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .build();
        } catch (SQLException e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }

    }
}
