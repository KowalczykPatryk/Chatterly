package chatapp.server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.DeploymentException;

import chatapp.server.config.DependencyBinder;
import chatapp.server.auth.JwtUtil;

import java.security.Key;
import java.io.IOException;
import java.net.URI;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.glassfish.tyrus.server.Server;
import chatapp.server.websocket.ChatWebSocketEndpoint;

public class Main {
    private static final String port = System.getenv("PORT");
    private static final int p = (port != null) ? Integer.parseInt(port) : 8081;
    private static final URI BASE_URI = URI.create("http://localhost:" + p + "/api/");

    public static void main(String[] args) throws IOException {
        String secretBase64 = System.getenv("JWT_SECRET");
        if (secretBase64 == null || secretBase64.isBlank()) {
            System.err.println("ERROR: JWT_SECRET env var is not set");
            System.exit(1);
        }
        // Decode and build your Key once
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
        Key jwtKey = Keys.hmacShaKeyFor(keyBytes);

        // Initialize JwtUtil
        JwtUtil.init(jwtKey);

        //Konfigurujemy, gdzie szukać klas z @Path
        ResourceConfig config = new ResourceConfig()
                .packages("chatapp.server.controller")
                .register(JacksonFeature.class)
                .register(new DependencyBinder());

        // Uruchamiamy Grizzly (Jersey) na wskazanym URI
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, config);

        Server wsServer = new Server("0.0.0.0", 8082, "/api", null, ChatWebSocketEndpoint.class);
        try {
            wsServer.start();
        } catch(DeploymentException  e) {}

        System.out.println("REST + WS running at " + BASE_URI);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {server.shutdownNow(); wsServer.stop();}));

        // Utrzymujemy wątek główny przy życiu
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
