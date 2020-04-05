package com.meemaw.rec.page.datasource;

import com.meemaw.rec.page.model.Page;
import com.meemaw.rec.page.model.PageIdentityDTO;
import com.meemaw.rec.page.model.PageIdentityDTOBuilder;
import com.meemaw.shared.rest.exception.DatabaseException;
import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Row;
import io.vertx.axle.sqlclient.RowIterator;
import io.vertx.axle.sqlclient.RowSet;
import io.vertx.axle.sqlclient.Tuple;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@Slf4j
public class PgPageDatasource implements PageDatasource {

  @Inject
  PgPool pgPool;

  private static final String SELECT_LINK_DEVICE_SESSION_RAW_SQL = "SELECT session_id FROM rec.page WHERE organization = $1 AND uid = $2 AND page_start > now() - INTERVAL '30 min' ORDER BY page_start DESC LIMIT 1;";

  public CompletionStage<Optional<UUID>> findDeviceSession(String orgId, UUID uid) {
    Tuple values = Tuple.of(orgId, uid);
    return pgPool.preparedQuery(SELECT_LINK_DEVICE_SESSION_RAW_SQL, values)
        .thenApply(this::extractSessionId)
        .exceptionally(throwable -> {
          log.error("Failed to findDeviceSession", throwable);
          throw new DatabaseException();
        });
  }

  private Optional<UUID> extractSessionId(RowSet<Row> pgRowSet) {
    RowIterator<Row> iterator = pgRowSet.iterator();
    if (!iterator.hasNext()) {
      return Optional.empty();
    }
    return Optional.of(iterator.next().getUUID(0));
  }

  private static final String INSERT_PAGE_RAW_SQL = "INSERT INTO rec.page (id, uid, session_id, organization, doctype, url, referrer, height, width, screen_height, screen_width, compiled_timestamp) VALUES($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12);";

  public CompletionStage<PageIdentityDTO> insertPage(UUID pageId, UUID uid, UUID sessionId,
      Page page) {
    Tuple values = Tuple.newInstance(io.vertx.sqlclient.Tuple.of(
        pageId,
        uid,
        sessionId,
        page.getOrganization(),
        page.getDoctype(),
        page.getUrl(),
        page.getReferrer(),
        page.getHeight(),
        page.getWidth(),
        page.getScreenHeight(),
        page.getScreenWidth(),
        page.getCompiledTimestamp()
    ));

    return pgPool.preparedQuery(INSERT_PAGE_RAW_SQL, values)
        .thenApply(x -> new PageIdentityDTOBuilder().setPageId(pageId).setSessionId(sessionId)
            .setUid(uid).build())
        .exceptionally(throwable -> {
          log.error("Failed to insertPage", throwable);
          throw new DatabaseException();
        });
  }
}
