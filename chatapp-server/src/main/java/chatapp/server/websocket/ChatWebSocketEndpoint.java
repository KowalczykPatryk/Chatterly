package chatapp.server.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import chatapp.server.session.ChatSessionRegistry;
import chatapp.server.model.Message;
import chatapp.server.dao.MessageDao;
import chatapp.server.utils.MessageDecoder;
import chatapp.server.utils.MessageEncoder;

import java.sql.*;
import java.io.IOException;
import jakarta.websocket.EncodeException;
import com.fasterxml.jackson.core.JsonProcessingException;

@ServerEndpoint(value="/ws/chat/{userFrom}/{userTo}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatWebSocketEndpoint {

    @OnOpen
    public void onOpen(Session session,
                       @PathParam("userFrom") String userFrom,
                       @PathParam("userTo")   String userTo) {
        session.getUserProperties().put("userFrom", userFrom);
        session.getUserProperties().put("userTo",   userTo);

        ChatSessionRegistry.register(userFrom, userTo, session);
    }

    @OnMessage
    public void onMessage(Session session, Message message)
    {
        try{
            String userFrom = (String) session.getUserProperties().get("userFrom");
            String userTo   = (String) session.getUserProperties().get("userTo");

            MessageDao messageDao = new MessageDao();
            messageDao.saveMessage(message);

            Session recipientSession = ChatSessionRegistry.get(userTo, userFrom);
            if (recipientSession != null && recipientSession.isOpen()) {
                recipientSession.getBasicRemote().sendObject(message);
            }
        } catch (SQLException | IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        String userFrom = (String) session.getUserProperties().get("userFrom");
        String userTo   = (String) session.getUserProperties().get("userTo");
        ChatSessionRegistry.unregister(userFrom, userTo);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }
}
