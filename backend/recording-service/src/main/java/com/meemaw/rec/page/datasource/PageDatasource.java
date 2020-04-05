package com.meemaw.rec.page.datasource;

import com.meemaw.rec.page.model.Page;
import com.meemaw.rec.page.model.PageIdentityDTO;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public interface PageDatasource {

  /**
   * @param orgId
   * @param uid
   * @return optionally linked sessionId that has been active in the last 30 minutes
   */
  CompletionStage<Optional<UUID>> findDeviceSession(String orgId, UUID uid);

  /**
   * @param pageId
   * @param uid
   * @param sessionId
   * @param page
   * @return newly created Page
   */
  CompletionStage<PageIdentityDTO> insertPage(UUID pageId, UUID uid, UUID sessionId,
      Page page);
}
