package com.meemaw.session.ws.v1;

import com.meemaw.shared.event.model.AbstractBrowserEvent;
import io.smallrye.reactive.messaging.annotations.Merge;
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

@ServerEndpoint(value = "/v1/sessions")
@ApplicationScoped
@Slf4j
public class SessionSocket {

  private final Map<String, Session> sessions = new ConcurrentHashMap<>();

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

  @Merge
  @Incoming("events")
  public void process(AbstractBrowserEvent event) {
    log.info("Incoming event {}", event);
    sessions.values().forEach(session -> {
      String sessionId = session.getId();
      String message = String.format("%d", event.getTimestamp());

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
