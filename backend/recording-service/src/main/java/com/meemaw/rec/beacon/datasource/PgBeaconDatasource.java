package com.meemaw.rec.beacon.datasource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.rec.beacon.model.Beacon;
import com.meemaw.shared.rest.exception.DatabaseException;
import com.meemaw.shared.rest.response.Boom;
import com.meemaw.shared.rest.status.MissingStatus;
import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Tuple;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class PgBeaconDatasource implements BeaconDatasource {

  @Inject
  PgPool pgPool;

  @Inject
  ObjectMapper objectMapper;

  private static final String INSERT_BEACON_RAW_SQL = "INSERT INTO rec.beacon (timestamp, sequence, events) VALUES($1, $2, $3)";

  @Override
  public CompletionStage<Void> store(Beacon beacon) {
    String jsonEvents;
    try {
      jsonEvents = objectMapper.writeValueAsString(beacon.getEvents());
    } catch (JsonProcessingException ex) {
      throw Boom.status(MissingStatus.UNPROCESSABLE_ENTITY).message(ex.getOriginalMessage())
          .exception();
    }
    Tuple values = Tuple.of(beacon.getTimestamp(), beacon.getSequence(), jsonEvents);
    return pgPool.preparedQuery(INSERT_BEACON_RAW_SQL, values)
        .thenAccept(nothing -> {
        })
        .exceptionally(throwable -> {
          log.error("Failed to store beacon", throwable);
          throw new DatabaseException();
        });
  }
}
