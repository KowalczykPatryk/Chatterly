package chatapp.server.session;

import jakarta.websocket.*;
import java.util.concurrent.*;

public class ChatSessionRegistry {

    private static final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();

    public static void register(String from, String to, Session session) {
        sessions.put(key(from,to), session);
    }

    public static Session get(String from, String to) {
        return sessions.get(key(from,to));
    }

    public static void unregister(String from, String to) {
        sessions.remove(key(from,to));
    }

    private static String key(String a, String b) {
        return a + "→" + b;
    }
}
