package chatapp.server.dao;

import chatapp.server.model.Message;
import java.util.List;

public class MessageDao
{
    void saveMessage(Message message){}
    List<Message> getMessagesBetweenUsers(int user1Id, int user2Id){return null;}
    List<Message> getUndeliveredMessages(int userId){return null;}
    void markAsDelivered(long messageId){}

}