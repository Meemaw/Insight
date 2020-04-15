package com.meemaw.session.ws.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.shared.event.kafka.EventsChannel;
import com.meemaw.shared.event.model.AbstractBrowserEvent;
import com.meemaw.shared.event.model.BrowserLoadEvent;
import com.meemaw.shared.event.model.BrowserUnloadEvent;
import io.smallrye.reactive.messaging.annotations.Merge;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ServerEndpoint(value = SessionSocketImpl.PATH)
@ApplicationScoped
@Slf4j
public class SessionSocketImpl {

  public static final String PATH = "/v1/sessions";

  private final Map<String, Session> sessions = new ConcurrentHashMap<>();

  @Inject
  ObjectMapper objectMapper;

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

  private void handleUnloadEvent(BrowserUnloadEvent event) {
    log.info("Unload event {}", event);
    handleEvent(event);
  }

  private void handleLoadEvent(BrowserLoadEvent event) {
    log.info("Load event {}", event);
    handleEvent(event);
  }

  private void handleEvent(AbstractBrowserEvent event) {
    try {
      String eventPayload = objectMapper.writeValueAsString(event);
      sessions.values().forEach(session -> dispatchEvent(session, eventPayload));
    } catch (JsonProcessingException ex) {
      log.error("Failed to serialize event {}", event, ex);
    }
  }

  private void dispatchEvent(Session session, String eventPayload) {
    String sessionId = session.getId();
    session.getAsyncRemote().sendObject(eventPayload, sendResult -> {
      if (sendResult.getException() != null) {
        log.error("Failed to send event {} to client {}", eventPayload, sessionId,
            sendResult.getException());
      } else {
        log.trace("Event {} sent to client {}", eventPayload, sessionId);
      }
    });
  }

  // TODO: have a separate channel for unload events and only read those
  @Merge
  @Incoming(EventsChannel.NAME)
  public void process(AbstractBrowserEvent event) {
    if (event instanceof BrowserLoadEvent) {
      handleLoadEvent((BrowserLoadEvent) event);
    } else if (event instanceof BrowserUnloadEvent) {
      handleUnloadEvent((BrowserUnloadEvent) event);
    }
  }

}
