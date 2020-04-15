package com.meemaw.session.ws.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.shared.event.kafka.EventsChannel;
import com.meemaw.shared.event.model.AbstractBrowserEvent;
import com.meemaw.test.matchers.SameJSON;
import com.meemaw.test.testconainers.kafka.KafkaResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.websocket.Session;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Tag("integration")
@QuarkusTestResource(KafkaResource.class)
public class SessionSocketEventTest extends AbstractSessionSocketTest {

  @Inject
  @Channel(EventsChannel.NAME)
  Emitter<AbstractBrowserEvent> emitter;

  @Inject
  ObjectMapper objectMapper;

  @Test
  public void eventMessageReception() throws Exception {
    try (Session session = connect()) {
      assertEquals(String.format("OPEN %s", session.getId()), MESSAGES.poll(10, TimeUnit.SECONDS));

      // load event
      String loadEventPayload = "{\"t\": 1234, \"e\": \"8\", \"a\": [\"http://localhost:8080\"]}";
      AbstractBrowserEvent loadEvent = objectMapper
          .readValue(loadEventPayload, AbstractBrowserEvent.class);
      emitter.send(loadEvent);
      SameJSON.assertEquals(loadEventPayload, MESSAGES.poll(10, TimeUnit.SECONDS));

      // unload event
      String unloadEventPayload = "{\"t\": 1234, \"e\": \"1\", \"a\": [\"http://localhost:8080\"]}";
      AbstractBrowserEvent unloadEvent = objectMapper
          .readValue(unloadEventPayload, AbstractBrowserEvent.class);
      emitter.send(unloadEvent);
      SameJSON.assertEquals(unloadEventPayload, MESSAGES.poll(10, TimeUnit.SECONDS));
    }
  }

}
