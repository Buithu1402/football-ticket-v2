package com.app.footballticketservice.model;

import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

import com.google.gson.Gson;
import lombok.extern.java.Log;

import java.util.logging.Level;

@Log
public class MessageDecoder implements Decoder.Text<Message> {

    private static final Gson gson = new Gson();

    @Override
    public Message decode(String s) {
        return gson.fromJson(s, Message.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
        endpointConfig.getUserProperties().forEach((k, v) -> log.log(Level.INFO, "Key: {0} Value: {1}", new Object[]{k, v}));
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
