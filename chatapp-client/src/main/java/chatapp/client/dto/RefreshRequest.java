package chatapp.client.dto;

public class RefreshRequest {
    private String refreshToken;


    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getRefreshToken() { return refreshToken; }
}
