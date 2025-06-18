package chatapp.server.dto;

import java.time.Instant;

public class FriendRequest{
    private String usernameFrom;
    private String usernameFriend;
    private Instant createdAt;

    public String getUsernameFrom() {return usernameFrom;}
    public void setUsernameFrom(String usernameFrom) {this.usernameFrom = usernameFrom;}

    public String getUsernameFriend() {return usernameFriend;}
    public void setUsernameFriend(String usernameFriend) {this.usernameFriend = usernameFriend;}

    public Instant getCreatedAt() {return createdAt;}
    public void setCreatedAt(Instant createdAt) {this.createdAt = createdAt;}
}