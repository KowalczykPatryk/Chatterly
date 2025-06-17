package chatapp.server.dto;

import java.time.Instant;

public class FriendRequest{
    private String usernameFrom;
    private String usernameFriend;
    private Instant createdAt;

    public int getUsernameFrom() {return usernameFrom;}
    public void setUsernameFrom(int usernameFrom) {this.usernameFrom = usernameFrom;}

    public int getUsernameFriend() {return usernameFriend;}
    public void setUsernameFriend(int usernameFriend) {this.usernameFriend = usernameFriend;}

    public Instant getCreatedAt() {return createdAt;}
    public void setCreatedAt(Instant createdAt) {this.createdAt = createdAt;}
}