package com.meemaw.rec.page.resource.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.rec.beacon.model.BeaconDTO;
import com.meemaw.rec.page.page.Page;
import com.meemaw.rec.page.page.PageDTO;
import com.meemaw.rec.page.service.PageService;
import com.meemaw.shared.rest.response.Boom;
import com.meemaw.shared.rest.response.DataResponse;
import com.meemaw.shared.rest.status.MissingStatus;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PageResourceImpl implements PageResource {

  @Inject
  PageService pageService;

  @Inject
  ObjectMapper objectMapper;

  @Inject
  Validator validator;

  @Override
  public CompletionStage<Response> page(String payload) {
    PageDTO pageDTO;
    try {
      pageDTO = objectMapper.readValue(payload, PageDTO.class);
    } catch (JsonProcessingException ex) {
      log.error("Failed to serialize page", ex);
      return CompletableFuture.completedFuture(Boom.status(MissingStatus.UNPROCESSABLE_ENTITY)
          .message(ex.getOriginalMessage())
          .response());
    }

    Set<ConstraintViolation<PageDTO>> constraintViolations = validator.validate(pageDTO);
    if (constraintViolations.size() > 0) {
      throw new ConstraintViolationException(constraintViolations);
    }

    Page page = Page.from(pageDTO);
    return pageService.process(page).thenApply(DataResponse::ok);
  }
}
