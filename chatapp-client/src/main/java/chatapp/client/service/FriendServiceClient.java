package chatapp.client.service;

import jakarta.ws.rs.core.Response;
import chatapp.client.storage.SQLiteManager;
import chatapp.client.dto.FriendAddRequest;
import chatapp.client.model.MyUsername;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.*;

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
        client.register(org.glassfish.jersey.jackson.JacksonFeature.class);
        Response response = client
                .target(baseUrl+"/requests")
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));

        client.close();

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

}