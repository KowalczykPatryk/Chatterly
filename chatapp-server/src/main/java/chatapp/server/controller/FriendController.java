package chatapp.server.controller;

import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.inject.Inject;


/**
 * Kontroler Jersey obsługujący operacje zarządzania znajomymi.
 */
@Path("/friends")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {
    @Inject
    private FriendService friendService;
    private UserService userService;

    /**
     * Endpoint przyjmowania friend request.
     * POST /api/friends/request
     * Body: { "usernameFrom": "...", "usernameTo": "...", "accessToken": "..." }
     */
    @POST
    @Path("/request")
    public Response request(FriendRequest req){

    }

    /**
     * Endpoint pobierania friend request.
     * POST /api/friends/request
     * Body: { "usernameTo": "...", "accessToken": "..." }
     */
    @GET
    @Path("/request")
    public Response requestGet(GetFriendsRequest req){

    }

    /**
     * Endpoint przyjmowania friend request response.
     * POST /api/friends/requestResponse
     * Body: { "usernameFrom": "...", "usernameTo": "...", "responseStatus": "...", "accessToken": "..." }
     */
    @POST
    @Path("/requestResponse")
    public Response requestResponse(FriendRequestResponse resp){

    }
    /**
     * Endpoint do zrywania znajomości.
     * POST /api/friends/remove
     * Body: { "usernameFrom": "...", "usernameTo": "...", "accessToken": "..." }
     */
    @POST
    @Path("/remove")
    public Response remove(RemoveFriendRequest req){

    }
    /**
     * Endpoint pobierania listy znajomych.
     * POST /api/friends/
     * * Body: { "username": "...", "accessToken": "..." }
     */
    @GET
    @Path("/")
    public Response getAllFriends(GetFriendsRequest req)
    {

    }
    /**
     * Endpoint pobierania statusu znajomych.
     * POST /api/friends/status
     * * Body: { "usernameFrom": "...", "usernameFriend": "...", "accessToken": "..." }
     */
    @GET
    @Path("/status")
    public Response getFriendStatus(GetFriendsStatusRequest req)
    {

    }
}