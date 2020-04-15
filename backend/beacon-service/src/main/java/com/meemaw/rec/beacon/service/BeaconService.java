package com.meemaw.rec.beacon.service;

import com.meemaw.rec.beacon.datasource.BeaconDatasource;
import com.meemaw.rec.beacon.datasource.PageDatasource;
import com.meemaw.rec.beacon.model.Beacon;
import com.meemaw.shared.event.kafka.EventsChannel;
import com.meemaw.shared.event.model.AbstractBrowserEvent;
import com.meemaw.shared.event.model.BrowserUnloadEvent;
import com.meemaw.shared.rest.response.Boom;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.eclipse.microprofile.reactive.messaging.OnOverflow.Strategy;

@ApplicationScoped
@Slf4j
public class BeaconService {

  @Inject
  BeaconDatasource beaconDatasource;

  @Inject
  PageDatasource pageDatasource;

  @Inject
  @Channel(EventsChannel.NAME)
  @OnOverflow(value = Strategy.UNBOUNDED_BUFFER)
  Emitter<AbstractBrowserEvent> eventsEmitter;

  public Uni<Void> pageEnd(UUID pageId) {
    return pageDatasource.pageEnd(pageId)
        .onItem()
        .apply(maybePageEnd -> {
          if (maybePageEnd.isEmpty()) {
            log.warn("Page end missing pageId={}", pageId);
          } else {
            log.info("Page end at {} for pageId={}", maybePageEnd.get(), pageId);
          }
          return null;
        });
  }

  public Uni<?> process(UUID sessionID, UUID uid, UUID pageID, Beacon beacon) {
    return pageDatasource.pageExists(sessionID, uid, pageID).onItem().produceUni(exists -> {
      if (!exists) {
        log.warn("Unlinked beacon sessionID={} uid={} pageId={}", sessionID, uid, pageID);
        throw Boom.badRequest().message("Unlinked beacon").exception();
      }

      List<AbstractBrowserEvent> events = beacon.getEvents();
      List<Uni<Void>> operations = writeToEventsChannel(events).collect(Collectors.toList());

      operations.add(beaconDatasource.store(beacon));

      // BrowserUnloadEvent always comes last!
      if (events.get(events.size() - 1) instanceof BrowserUnloadEvent) {
        operations.add(pageEnd(pageID));
      }

      return Uni.combine().all().unis(operations).combinedWith(nothing -> null);
    });
  }

  private Stream<Uni<Void>> writeToEventsChannel(List<AbstractBrowserEvent> events) {
    return events.stream()
        .map(event -> Uni.createFrom().completionStage(eventsEmitter.send(event))
            .onFailure()
            .apply(throwable -> {
              log.error("Something went wrong while sending event to Kafka topic", throwable);
              return null;
            }));
  }
}
