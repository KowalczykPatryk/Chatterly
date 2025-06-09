package chatapp.server.dto;

public class Tokens
{
    private String accessToken;
    private String refreshToken;

    public Tokens(String accessToken, String refreshToken)
    {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getAccessToken() { return accessToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getRefreshToken() { return refreshToken; }
}