package chatapp.client.dto;

import java.util.List;

public class GetUsernamesResponse {
    private List<String> usernames;

    public GetUsernamesResponse() {}
    public GetUsernamesResponse(List<String> usernames) {this.usernames = usernames;}

    public List<String> getUsernames() {
        return usernames;
    }
    public void setUsernames(List<String> usernames) {this.usernames = usernames;}
}