package com.meemaw.page.resource.v1.page;

import com.meemaw.page.model.page.Page;
import com.meemaw.page.model.page.PageDTO;
import com.meemaw.page.service.page.PageService;
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
