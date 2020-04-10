package com.meemaw.session.datasource;

import com.meemaw.session.model.Page;
import com.meemaw.session.model.PageDTO;
import com.meemaw.session.model.PageSessionDTO;
import io.smallrye.mutiny.Uni;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PageDatasource {

  /**
   * @param orgId
   * @param uid
   * @return optionally linked sessionId that has been active in the last 30 minutes
   */
  Uni<Optional<UUID>> findDeviceSession(String orgId, UUID uid);

  /**
   * @param pageId
   * @param uid
   * @param sessionId
   * @param page
   * @return newly created Page
   */
  Uni<PageSessionDTO> insertPage(UUID pageId, UUID uid, UUID sessionId, Page page);

}
