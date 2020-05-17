package com.meemaw.session.datasource;

import com.meemaw.session.model.CreatePageDTO;
import com.meemaw.session.model.PageDTO;
import com.meemaw.session.model.PageIdentity;
import io.smallrye.mutiny.Uni;
import java.util.Optional;
import java.util.UUID;

public interface PageDatasource {

  /**
   * @param orgId organization id
   * @param uid user id
   * @return optionally linked sessionID that has been a ctive in the last 30 minutes
   */
  Uni<Optional<UUID>> findUserSessionLink(String orgId, UUID uid);

  /**
   * @param pageId page id
   * @param uid user id
   * @param sessionId session id
   * @param page page
   * @return newly created Page
   */
  Uni<PageIdentity> insertPage(UUID pageId, UUID uid, UUID sessionId, CreatePageDTO page);

  /** @return number of currently active pages */
  Uni<Integer> activePageCount();

  /**
   * @param pageId page id
   * @param sessionId session id
   * @param orgId organization id
   * @return page
   */
  Uni<Optional<PageDTO>> getPage(UUID pageId, UUID sessionId, String orgId);
}
