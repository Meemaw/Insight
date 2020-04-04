package com.meemaw.rec.service.beacon;

import com.meemaw.rec.model.beacon.Beacon;
import com.meemaw.shared.rest.exception.DatabaseException;
import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@Slf4j
public class BeaconService {

  @Inject
  PgPool pgPool;

  private static final String INSERT_BEACON_RAW_SQL = "INSERT INTO rec.beacon (timestamp, sequence) VALUES($1, $2)";

  public CompletionStage<Beacon> process(Beacon beacon) {
    Tuple values = Tuple.of(beacon.getTimestamp(), beacon.getSequence());
    return pgPool.preparedQuery(INSERT_BEACON_RAW_SQL, values)
        .thenApply(pgRowSet -> beacon)
        .exceptionally(throwable -> {
          log.error("Failed to store beacon", throwable);
          throw new DatabaseException();
        });
  }
}
