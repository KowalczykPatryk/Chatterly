package chatapp.client.dto;

public class FriendRequestResponse
{
    private String usernameFrom;
    private String usernameTo;
    private boolean status;

    public FriendRequestResponse() {}
    public FriendRequestResponse(String usernameFrom, String usernameTo,  boolean status)
    {
        this.usernameFrom = usernameFrom;
        this.usernameTo = usernameTo;
        this.status = status;
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
    public boolean getStatus()
    {
        return status;
    }
    public void setStatus(boolean status)
    {
        this.status = status;
    }
}