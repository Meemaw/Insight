package com.meemaw.rec.page.resource.v1;

import com.meemaw.rec.page.page.Page;
import com.meemaw.rec.page.page.PageDTO;
import com.meemaw.rec.page.service.PageService;
import com.meemaw.shared.rest.response.DataResponse;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PageResourceImpl implements PageResource {

  @Inject
  PageService pageService;

  @Override
  public CompletionStage<Response> page(PageDTO payload) {
    Page page = Page.from(payload);
    return pageService.process(page).thenApply(DataResponse::ok);
  }
}
