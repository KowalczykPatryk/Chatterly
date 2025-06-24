package chatapp.client.websocket;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import jakarta.websocket.OnMessage;
import javafx.application.Platform;


import java.util.*;

import chatapp.client.utils.MessageEncoder;
import chatapp.client.utils.MessageDecoder;
import chatapp.client.storage.SQLiteManager;
import chatapp.client.model.Message;
import chatapp.client.crypto.CryptoUtil;
import chatapp.client.crypto.KeyStoreManager;
import chatapp.client.model.MyUsername;

import java.util.function.Consumer;
import java.net.URI;
import java.io.IOException;

public class ChatClient {

    private Session session;
    private Consumer<String> messageConsumer;
    private String receiverUsername;
    private byte[] symetricKey;
    private byte[] encryptedSymetricKey;

    public ChatClient(Consumer<String> onMessageConsumer) {
        this.messageConsumer = onMessageConsumer;
    }

    @ClientEndpoint(encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class })
    public class Endpoint {

        @OnOpen
        public void onOpen(Session session) throws Exception {
            ChatClient.this.session = session;
        }
        @OnMessage
        public void onMessage(Message message) throws Exception {
            String plainText = CryptoUtil.decryptContent(message.getCiphertext(), CryptoUtil.decryptSymKey(message.getEncryptedKey(), KeyStoreManager.getInstance().getPrivateKey(MyUsername.getMyUsername())), message.getIv());
            if (messageConsumer != null) {
                Platform.runLater(() -> messageConsumer.accept(plainText));
            }
        }
    }

    public void openChatWebSocket(String friendUsername) throws Exception {
        receiverUsername = friendUsername;
        SQLiteManager dbManager = SQLiteManager.getInstance();
        symetricKey = CryptoUtil.generateSymmetricKey();
        String userFrom = MyUsername.getMyUsername();
        encryptedSymetricKey = CryptoUtil.encryptWithPublicKey(symetricKey, dbManager.getPublicKey(userFrom, friendUsername));
        String uri = String.format("ws://localhost:8082/api/ws/chat/%s/%s", userFrom, friendUsername);

        // otwieranie połączenia
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        Endpoint clientEndpoint = new Endpoint();
        session = container.connectToServer(clientEndpoint, URI.create(uri));
    }
    public void sendMessage(String content) throws Exception {
        if (session == null || !session.isOpen()) {
            throw new IllegalStateException("WebSocket session is not open");
        }
        byte[] iv = CryptoUtil.generateIv();
        byte[] ciphertext = CryptoUtil.encryptContent(symetricKey, iv, content);

        Message msg = new Message(MyUsername.getMyUsername(), receiverUsername, iv, encryptedSymetricKey, ciphertext);
        session.getBasicRemote().sendObject(msg);
    }
    public void close() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
