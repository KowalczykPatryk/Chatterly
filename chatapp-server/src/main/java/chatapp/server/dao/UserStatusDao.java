package chatapp.server.dao;

import java.sql.Timestamp;

public class UserStatusDao
{
    void setUserOnline(int userId){}
    void setUserOffline(int userId, Timestamp lastSeen){}
    boolean isUserOnline(int userId){return false;}
    Timestamp getLastSeen(int userId){return null;}

}