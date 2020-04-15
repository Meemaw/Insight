package com.meemaw.session.resource.v1;

import com.meemaw.session.model.Page;
import com.meemaw.session.model.PageDTO;
import com.meemaw.session.service.PageService;
import com.meemaw.shared.rest.response.DataResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

public class SessionResourceImpl implements SessionResource {

  @Inject
  PageService pageService;

  @Override
  public CompletionStage<Response> page(PageDTO payload) {
    return pageService.process(Page.from(payload))
        .subscribeAsCompletionStage()
        .thenApply(DataResponse::ok);
  }

  @Override
  public CompletionStage<Response> count() {
    return pageService.activePageCount()
        .subscribeAsCompletionStage()
        .thenApply(DataResponse::ok);
  }
}
