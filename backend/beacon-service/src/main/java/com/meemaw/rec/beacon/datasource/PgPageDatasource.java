package com.meemaw.rec.beacon.datasource;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class PgPageDatasource implements PageDatasource {

  @Inject
  PgPool pgPool;

  private static final String PAGE_EXISTS_RAW_SQL = "SELECT 1 FROM rec.page WHERE session_id = $1 AND uid = $2 AND id = $3";

  @Override
  public Uni<Boolean> pageExists(UUID sessionID, UUID uid, UUID pageID) {
    Tuple values = Tuple.of(sessionID, uid, pageID);
    return pgPool.preparedQuery(PAGE_EXISTS_RAW_SQL, values)
        .map(row -> row.iterator().hasNext());
  }
}
