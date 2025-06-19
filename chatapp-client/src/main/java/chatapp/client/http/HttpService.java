package chatapp.client.http;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import chatapp.client.model.ApiResponse;

public class HttpService {

    private final Client client;

    public HttpService() {
        this.client = ClientBuilder.newClient();
        this.client.register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }
    public <T> ApiResponse<T> post(String url, Object body, Class<T> respClass) {
        Response response = client
                .target(url)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        int status = response.getStatus();
        T bodyObj = response.readEntity(respClass);

        return new ApiResponse<>(status, bodyObj);
    }


    public void close() {
        client.close();
    }
}

