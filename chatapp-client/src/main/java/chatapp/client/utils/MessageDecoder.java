package chatapp.client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;
import chatapp.client.model.Message;
import jakarta.websocket.DecodeException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MessageDecoder implements Decoder.Text<Message> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Message decode(String s) throws DecodeException {
        try {
            return mapper.readValue(s, Message.class);
        } catch (Exception e) {
            // wrap any JSON processing exception in DecodeException
            throw new DecodeException(s, "Failed to decode JSON to Message", e);
        }
    }
    @Override public boolean willDecode(String s) { return s != null; }
    @Override public void init(EndpointConfig cfg) { }
    @Override public void destroy() { }
}
