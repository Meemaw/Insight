package com.meemaw.session.datasource;

import com.meemaw.session.model.Page;
import com.meemaw.session.model.PageIdentity;
import io.smallrye.mutiny.Uni;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PageDatasource {

  /**
   * @param orgId
   * @param uid
   * @return optionally linked sessionID that has been a ctive in the last 30 minutes
   */
  Uni<Optional<UUID>> findUserSessionLink(String orgId, UUID uid);

  /**
   * @param pageId
   * @param uid
   * @param sessionId
   * @param page
   * @return newly created Page
   */
  Uni<PageIdentity> insertPage(UUID pageId, UUID uid, UUID sessionId, Page page);

  /**
   * @return number of currently active pages
   */
  Uni<Integer> activePageCount();

}
