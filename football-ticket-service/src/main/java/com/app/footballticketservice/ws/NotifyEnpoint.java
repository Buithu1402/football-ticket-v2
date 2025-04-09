package com.app.footballticketservice.ws;

import com.app.footballticketservice.model.Message;
import com.app.footballticketservice.model.MessageDecoder;
import com.app.footballticketservice.model.MessageEncoder;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint(value = "/notify/{username}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class NotifyEnpoint {
    private Session session;
    private static final Set<NotifyEnpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static final HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("username") String username
    ) throws IOException, EncodeException {

        this.session = session;
        chatEndpoints.add(this);
        users.put(session.getId(), username);

        Message message = new Message();
        message.setFrom(username);
        message.setContent("Connected!");
        broadcast(message);
    }

    @OnMessage
    public void onMessage(Session session, Message message)
            throws IOException, EncodeException {

        message.setFrom(users.get(session.getId()));
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {

        chatEndpoints.remove(this);
        Message message = new Message();
        message.setFrom(users.get(session.getId()));
        message.setContent("Disconnected!");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    public static void broadcast(Message message)
            throws IOException, EncodeException {

        chatEndpoints.forEach((endpoint) -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                                    sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
