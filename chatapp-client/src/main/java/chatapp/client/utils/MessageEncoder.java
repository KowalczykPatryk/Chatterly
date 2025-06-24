package chatapp.client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import chatapp.client.model.Message;
import jakarta.websocket.EncodeException;


public class MessageEncoder implements Encoder.Text<Message> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void init(EndpointConfig config) {}

    @Override
    public String encode(Message message) throws EncodeException {
        try {
            // zamienia obiekt na JSON
            return MAPPER.writeValueAsString(message);
        } catch (Exception e) {
            throw new EncodeException(message, "Failed to encode Message to JSON", e);
        }
    }

    @Override
    public void destroy() {}
}
