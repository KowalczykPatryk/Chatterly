package chatapp.client.dto;

public class RefreshResponse
{
    private boolean success;
    private String accessToken;
    private String refreshToken;
    private String message;

    public RefreshResponse() { }

    public RefreshResponse(boolean success, String accessToken, String refreshToken, String message) {
        this.success = success;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.message = message;
    }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken(){ return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}