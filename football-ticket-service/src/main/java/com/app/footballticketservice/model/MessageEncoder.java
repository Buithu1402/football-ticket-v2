package com.app.footballticketservice.model;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import com.google.gson.Gson;
import jakarta.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {

    private static final Gson gson = new Gson();

    @Override
    public String encode(Message message) throws EncodeException {
        return gson.toJson(message);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
