package com.meemaw.rec.resource.v1.beacon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.rec.model.beacon.Beacon;
import com.meemaw.rec.model.beacon.BeaconDTO;
import com.meemaw.shared.rest.response.Boom;
import com.meemaw.shared.rest.response.DataResponse;
import com.meemaw.rec.service.beacon.BeaconService;

import com.meemaw.shared.rest.status.MissingStatus;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeaconResourceImpl implements BeaconResource {

  @Inject
  BeaconService beaconService;

  @Inject
  ObjectMapper objectMapper;

  @Override
  public CompletionStage<Response> beacon(String payload) {
    BeaconDTO beaconDTO;
    try {
      beaconDTO = objectMapper.readValue(payload, BeaconDTO.class);
    } catch (JsonProcessingException ex) {
      log.error("Failed to serialize beacon", ex);
      return CompletableFuture.completedFuture(Boom.status(MissingStatus.UNPROCESSABLE_ENTITY)
          .message(ex.getOriginalMessage())
          .response());
    }
    Beacon beacon = Beacon.from(beaconDTO);
    return beaconService.process(beacon).thenApply(DataResponse::ok);
  }


}
