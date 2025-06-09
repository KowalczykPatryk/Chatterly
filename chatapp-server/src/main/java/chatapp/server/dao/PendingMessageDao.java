package chatapp.server.dao;

import chatapp.server.model.Message;
import java.util.List;

public class PendingMessageDao
{
    void queuePendingMessage(Message message){}
    List<Message> getPendingMessagesForUser(int userId){return null;}
    void removePendingMessage(long messageId){}

}
