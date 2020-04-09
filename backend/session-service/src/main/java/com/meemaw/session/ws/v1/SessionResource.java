package com.meemaw.session.ws.v1;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ServerEndpoint("/v1/sessions")
@ApplicationScoped
@Slf4j
public class SessionResource {

  private Map<String, Session> sessions = new ConcurrentHashMap<>();

  @OnOpen
  public void onOpen(Session session) {
    sessions.put(session.getId(), session);
    log.info("onOpen {}", session.getId());
  }

  @OnClose
  public void onClose(Session session) {
    sessions.remove(session.getId());
    log.info("onClose {}", session.getId());
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    sessions.remove(session.getId());
    log.error("onError {}", session.getId(), throwable);
  }

  @Incoming("events")
  public void process(String test) {
    log.info("Incoming event {}", test);
    sessions.values().forEach(session -> {
      String message = String.valueOf(test);
      String sessionId = session.getId();
      session.getAsyncRemote().sendText(message, sendResult -> {
        if (sendResult.getException() != null) {
          log.error("Failed to send message to client {}", sessionId, sendResult.getException());
        } else {
          log.trace("Send message {} to client {}", message, sessionId);
        }
      });
    });
  }

}
