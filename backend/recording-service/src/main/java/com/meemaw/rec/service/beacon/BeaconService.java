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

  public CompletionStage<Beacon> process(Beacon beacon) {
    String rawSQL = "INSERT INTO rec.beacon (timestamp, sequence) VALUES($1, $2)";

    Tuple values = Tuple.of(
        beacon.getTimestamp(),
        beacon.getSequence()
    );

    return pgPool.preparedQuery(rawSQL, values)
        .thenApply(pgRowSet -> beacon)
        .exceptionally(throwable -> {
          log.error("Failed to store beacon", throwable);
          throw new DatabaseException();
        });
  }
}
