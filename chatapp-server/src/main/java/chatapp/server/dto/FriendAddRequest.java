package chatapp.server.dto;

public class FriendAddRequest
{
    private String usernameFrom;
    private String usernameTo;

    public FriendAddRequest() {}
    public FriendAddRequest(String usernameFrom, String usernameTo)
    {
        this.usernameFrom = usernameFrom;
        this.usernameTo = usernameTo;
    }
    public String getUsernameFrom()
    {
        return usernameFrom;
    }
    public void setUsernameFrom(String usernameFrom)
    {
        this.usernameFrom = usernameFrom;
    }
    public String getUsernameTo()
    {
        return usernameTo;
    }
    public  void setUsernameTo(String usernameTo)
    {
        this.usernameTo = usernameTo;
    }
}