package chatapp.server.model;

public class User {
    private String username;
    private String password;
    private String publicKey;

    // potrzebny dla JacksonFeature aby mógł utworzyć obiekt bazowy i ustawić zmienne z JSON czy zdeserializować JSON, który przyjdzie z żądania, na obiekt klasy
    public User() { }

    public User(String username, String password, String publicKey) {
        this.username = username;
        this.password = password;
        this.publicKey = publicKey;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPublicKey() {return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
}
