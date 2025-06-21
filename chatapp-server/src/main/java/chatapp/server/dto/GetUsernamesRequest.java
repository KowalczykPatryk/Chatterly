package chatapp.server.dto;

public class GetUsernamesRequest {
    private String accessToken;
    private String searchTerm;

    public GetUsernamesRequest() {}
    public GetUsernamesRequest(String accessToken, String searchTerm) {
        this.accessToken = accessToken;
        this.searchTerm = searchTerm;
    }

    public String getAccessToken() {return accessToken;}
    public void setAccessToken(String accessToken) {this.accessToken = accessToken;}

    public String getSearchTerm() {return searchTerm;}
    public void setSearchTerm(String searchTerm) {this.searchTerm = searchTerm;}
}