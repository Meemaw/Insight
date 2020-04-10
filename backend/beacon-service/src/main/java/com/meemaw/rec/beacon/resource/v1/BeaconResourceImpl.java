package com.meemaw.rec.beacon.resource.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.rec.beacon.datasource.PageDatasource;
import com.meemaw.rec.beacon.model.Beacon;
import com.meemaw.rec.beacon.model.BeaconDTO;
import com.meemaw.shared.rest.response.Boom;
import com.meemaw.rec.beacon.service.BeaconService;

import com.meemaw.shared.rest.status.MissingStatus;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeaconResourceImpl implements BeaconResource {

  @Inject
  BeaconService beaconService;

  @Inject
  ObjectMapper objectMapper;

  @Inject
  Validator validator;

  @Override
  public CompletionStage<Response> beacon(
      UUID sessionID,
      UUID userID,
      UUID pageID,
      String payload) {
    BeaconDTO beaconDTO;
    try {
      beaconDTO = objectMapper.readValue(payload, BeaconDTO.class);
    } catch (JsonProcessingException ex) {
      log.error("Failed to serialize beacon", ex);
      return CompletableFuture.completedFuture(Boom.status(MissingStatus.UNPROCESSABLE_ENTITY)
          .message(ex.getOriginalMessage())
          .response());
    }

    Set<ConstraintViolation<BeaconDTO>> constraintViolations = validator.validate(beaconDTO);
    if (constraintViolations.size() > 0) {
      throw new ConstraintViolationException(constraintViolations);
    }

    return beaconService.process(sessionID, userID, pageID, Beacon.from(beaconDTO))
        .subscribeAsCompletionStage()
        .thenApply(nothing -> Response.noContent().build());
  }
}
