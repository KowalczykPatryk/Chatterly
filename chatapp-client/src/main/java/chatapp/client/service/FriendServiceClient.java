package chatapp.client.service;

import jakarta.ws.rs.core.Response;
import chatapp.client.storage.SQLiteManager;
import chatapp.client.dto.FriendAddRequest;
import chatapp.client.model.MyUsername;
import chatapp.client.dto.FriendRequestResponse;
import chatapp.client.model.Friend;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.GenericType;

import java.sql.*;
import java.util.List;

public class FriendServiceClient {
    private final String baseUrl = "http://localhost:8081/api/friends";
    private SQLiteManager dbManager;
    private final Client client;

    public FriendServiceClient() {
        try {
            this.dbManager = SQLiteManager.getInstance();
        }catch(SQLException e){}
        this.client = ClientBuilder.newBuilder()
                .register(org.glassfish.jersey.jackson.JacksonFeature.class)
                .build();
    }

    public boolean sendInvitationTo(String username)
    {
        FriendAddRequest req = new FriendAddRequest(MyUsername.getMyUsername(), username);
        String accessToken;
        try {
            accessToken = dbManager.getAccessToken();
        } catch(SQLException e)
        {
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
        Response response = client
                .target(baseUrl+"/requests")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));

        if (response.getStatus() == Response.Status.CREATED.getStatusCode())
        {
            return true;
        }
        else if(response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode())
        {
            System.out.println("Wrong access token");
        }
        else if(response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
        {
            System.out.println("Invitation request failed");
        }
        System.out.println("get there");
        return false;
    }
    public String getFriendshipStatus(String username)
    {
        try {
            String accessToken = dbManager.getAccessToken();
            Response response = client
                    .target(baseUrl+"/"+username+"/status/"+MyUsername.getMyUsername())
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode())
            {
                return response.readEntity(String.class);
            }
            else if(response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode())
            {
                System.out.println("Wrong access token");
            }
            else if(response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
            {
                System.out.println("Getting status failed");
            }
        } catch(SQLException e)
        {
            System.out.println("SQLException: " + e.getMessage());
        }
        return "";
    }
    public List<String> getFriendshipRequests()
    {
        try {
            String accessToken = dbManager.getAccessToken();
            Response response = client
                    .target(baseUrl+"/requests/incoming/"+MyUsername.getMyUsername())
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode())
            {
                return response.readEntity(new GenericType<List<String>>() {});
            }
            else if(response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode())
            {
                System.out.println("Wrong access token");
            }
            else if(response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
            {
                System.out.println("Getting status failed");
            }
        } catch(SQLException e)
        {
            System.out.println("SQLException: " + e.getMessage());
        }
        return null;
    }
    public boolean respondToFriendRequest(String username, boolean accept)
    {
        try {
            String accessToken = dbManager.getAccessToken();
            FriendRequestResponse res = new FriendRequestResponse(MyUsername.getMyUsername(), username, accept);
            Response response = client
                    .target(baseUrl+"/requests/respond")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .post(Entity.entity(res, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode())
            {
                return true;
            }
            else if(response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode())
            {
                System.out.println("Wrong access token");
            }
            else if(response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
            {
                System.out.println("Sending response to friendhship request failed");
            }
        } catch(SQLException e)
        {
            System.out.println("SQLException: " + e.getMessage());
        }
        return false;
    }
    public List<Friend> getFriends()
    {
        try {
            String accessToken = dbManager.getAccessToken();
            Response response = client
                    .target(baseUrl+"/"+MyUsername.getMyUsername()+"/list")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode())
            {
                List<Friend> friends = response.readEntity(new GenericType<List<Friend>>() {});
                for (Friend friend: friends)
                {
                    dbManager.upsertFriend(friend.getUsername(), friend.getPublicKey());
                }
                return friends;
            }
            else if(response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode())
            {
                System.out.println("Wrong access token");
            }
            else if(response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
            {
                System.out.println("Getting friends failed");
            }
        } catch(SQLException e)
        {
            System.out.println("SQLException: " + e.getMessage());
        }
        return null;
    }

}