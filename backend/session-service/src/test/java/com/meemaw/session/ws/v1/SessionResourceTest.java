package com.meemaw.session.ws.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.shared.event.model.AbstractBrowserEvent;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Tag("integration")
@QuarkusTestResource(KafkaResource.class)
public class SessionResourceTest {

  @Inject
  ObjectMapper objectMapper;

  @Inject
  @Channel("events")
  Emitter<AbstractBrowserEvent> emitter;

  private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

  @TestHTTPResource("/v1/sessions")
  URI uri;

  @BeforeEach
  public void cleanup() {
    MESSAGES.clear();
  }

  @Test
  public void testConnection() throws Exception {
    try (Session session = ContainerProvider.getWebSocketContainer()
        .connectToServer(Client.class, uri)) {
      assertEquals(String.format("OPEN %s", session.getId()), MESSAGES.poll(10, TimeUnit.SECONDS));
      session.close();
      assertEquals(String.format("CLOSE %s", session.getId()), MESSAGES.poll(10, TimeUnit.SECONDS));
    }
  }

  @Test
  public void eventMessageReception() throws Exception {
    try (Session session = ContainerProvider.getWebSocketContainer()
        .connectToServer(Client.class, uri)) {
      assertEquals(String.format("OPEN %s", session.getId()), MESSAGES.poll(10, TimeUnit.SECONDS));

      AbstractBrowserEvent abstractBrowserEvent = objectMapper
          .readValue("{\"t\": 1234, \"e\": 1, \"a\": [\"http://localhost:8080\"]}",
              AbstractBrowserEvent.class);

      emitter.send(abstractBrowserEvent).exceptionally(error -> {
        System.out.println(error);
        throw new RuntimeException(error);
      }).thenRun(() -> System.out.println("Message emitted!"));
      System.out.println("After send!");

      assertEquals("MESSAGE 1234", MESSAGES.poll(10, TimeUnit.SECONDS));
    }
  }


  @ClientEndpoint
  public static class Client {

    @OnOpen
    public void open(Session session) {
      MESSAGES.add(String.format("OPEN %s", session.getId()));
    }

    @OnClose
    public void onClose(Session session) {
      MESSAGES.add(String.format("CLOSE %s", session.getId()));
    }

    @OnMessage
    void message(String msg) {
      MESSAGES.add(String.format("MESSAGE %s", msg));
    }
  }

}
