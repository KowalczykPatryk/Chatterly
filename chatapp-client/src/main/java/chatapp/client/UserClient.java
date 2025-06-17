package chatapp.client;

import chatapp.client.model.User;
import chatapp.client.dto.LoginRequest;
import chatapp.client.dto.LoginResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

/**
 * Klient Jersey do wywoływania endpointów UserController.
 */
public class UserClient {
    private static final String BASE_URI = "http://localhost:8080/chatapp/api/users";
    private final Client client;

    public UserClient() {
        this.client = ClientBuilder.newClient();
        // Jeśli używasz Jacksona, upewnij się, że w classpath jest jersey-media-json-jackson
        client.register(org.glassfish.jersey.jackson.JacksonFeature.class);
    }

    /**
     * Rejestracja nowego użytkownika.
     * Zwraca true, jeśli status HTTP 201 CREATED, w innym przypadku false.
     */
    public boolean register(User user) {
        Response response = client.target(BASE_URI)
                .path("register")
                .request()
                .post(Entity.json(user));

        int status = response.getStatus();
        response.close();

        return status == Response.Status.CREATED.getStatusCode();
    }

    /**
     * Logowanie użytkownika. Zwraca token (String) lub null, jeśli logowanie nieudane.
     */
    public String login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);

        Response response = client.target(BASE_URI)
                .path("login")
                .request()
                .post(Entity.json(request));

        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            response.close();
            return null;
        }

        // Parsujemy odpowiedź na obiekt LoginResponse
        LoginResponse loginResp = response.readEntity(LoginResponse.class);
        response.close();

        if (loginResp.isSuccess()) {
            return loginResp.getToken();
        } else {
            return null;
        }
    }

    /**
     * Sprawdzenie poprawności tokena.
     * Zwraca true jeśli status 200 OK, w innym wypadku false.
     */
    public boolean validateToken(String username, String token) {
        Response response = client.target(BASE_URI)
                .path("validate")
                .path(username)
                .request()
                .header("Authorization", "Bearer " + token)
                .get();

        int status = response.getStatus();
        response.close();
        return status == Response.Status.OK.getStatusCode();
    }

    public void close() {
        client.close();
    }

    // Przykładowe wywołanie (np. w main aplikacji):
    public static void main(String[] args) {
        UserClient userClient = new UserClient();

        // Przykład rejestracji
        User newUser = new User("john_doe", "password123");
        boolean registered = userClient.register(newUser);
        System.out.println("Registered: " + registered);

        // Przykład logowania
        String token = userClient.login("john_doe", "password123");
        System.out.println("Login token: " + token);

        // Przykład walidacji tokenu
        if (token != null) {
            boolean valid = userClient.validateToken("john_doe", token);
            System.out.println("Token valid: " + valid);
        }

        userClient.close();
    }
}
