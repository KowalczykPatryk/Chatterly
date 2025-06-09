package chatapp.server.dao;

import chatapp.server.db.Database;
import chatapp.server.model.User;
import java.util.List;

public class FriendDao
{
    void sendFriendRequest(int userId, int friendId){}
    void acceptFriendRequest(int userId, int friendId){}
    void removeFriend(int userId, int friendId){}
    List<User> getFriends(int userId){return null;}
    //List<FriendRequest> getPendingRequests(int userId);
    String getFriendshipStatus(int userId, int friendId){return null;}
}