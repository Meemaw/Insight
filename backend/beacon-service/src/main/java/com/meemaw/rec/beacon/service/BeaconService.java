package com.meemaw.rec.beacon.service;

import com.meemaw.rec.beacon.datasource.BeaconDatasource;
import com.meemaw.rec.beacon.datasource.PageDatasource;
import com.meemaw.rec.beacon.model.Beacon;
import com.meemaw.shared.event.kafka.EventsChannel;
import com.meemaw.shared.event.model.AbstractBrowserEvent;
import com.meemaw.shared.rest.response.Boom;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
@Slf4j
public class BeaconService {

  @Inject
  BeaconDatasource beaconDatasource;

  @Inject
  PageDatasource pageDatasource;

  @Inject
  @Channel(EventsChannel.NAME)
  Emitter<AbstractBrowserEvent> eventsEmitter;

  public Uni<Uni<Void>> process(UUID sessionID, UUID uid, UUID pageID, Beacon beacon) {
    return pageDatasource.pageExists(sessionID, uid, pageID).onItem().produceUni(exists -> {
      if (!exists) {
        log.warn("Unlinked beacon sessionID={} uid={} pageId={}", sessionID, uid, pageID);
        throw Boom.badRequest().message("Unlinked beacon").exception();
      }

      Multi<Uni<Void>> beaconWrite = Multi.createFrom()
          .uni(Uni.createFrom().item(beaconDatasource.store(beacon)));

      Multi<Uni<Void>> eventWrites = Multi.createFrom().iterable(beacon.getEvents())
          .onItem()
          .apply(event -> {
            return Uni.createFrom().completionStage(eventsEmitter.send(event))
                .onFailure()
                .apply(throwable -> {
                  log.error("Something went wrong while sending event to Kafka topic", throwable);
                  return null;
                })
                .onItem()
                .apply(item -> null);
          });

      return Multi
          .createBy()
          .concatenating()
          .streams(eventWrites, beaconWrite)
          .collectItems()
          .last();
    });
  }
}
