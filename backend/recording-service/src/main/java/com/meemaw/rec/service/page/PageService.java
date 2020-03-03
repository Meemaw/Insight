package com.meemaw.rec.service.page;

import com.meemaw.rec.datasource.page.PageDatasource;
import com.meemaw.rec.model.page.Page;
import com.meemaw.rec.model.page.PageIdentityDTO;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class PageService {

  @Inject
  PageDatasource pageDatasource;

  public CompletionStage<PageIdentityDTO> process(Page page) {
    UUID pageId = UUID.randomUUID();
    UUID uid = Optional.ofNullable(page.getUid()).orElseGet(UUID::randomUUID);
    String org = page.getOrganization();

    // unrecognized device; start a new session
    if (uid != page.getUid()) {
      UUID sessionId = UUID.randomUUID();
      log.info("Generating new session {} uid {} pageId {} org {}", sessionId, uid, pageId, org);
      return pageDatasource.insertPage(pageId, uid, sessionId, page);
    }

    // recognized device; try to link it with an existing session
    return pageDatasource.findDeviceSession(org, uid).thenCompose(
        maybeSessionId -> {
          if (maybeSessionId == null) {
            maybeSessionId = UUID.randomUUID();
            log.info("Could not link session for uid {}, pageId {} org {}", uid, pageId, org);
          } else {
            log.info("Session {} linked for uid {}, pageId {} org {}", maybeSessionId, uid, pageId,
                org);
          }
          return pageDatasource.insertPage(pageId, uid, maybeSessionId, page);
        }
    );
  }

}
