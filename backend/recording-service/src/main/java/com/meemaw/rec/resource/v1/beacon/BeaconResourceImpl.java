package com.meemaw.rec.resource.v1.beacon;

import com.meemaw.rec.model.beacon.Beacon;
import com.meemaw.rec.model.beacon.BeaconDTO;
import com.meemaw.shared.rest.response.DataResponse;
import com.meemaw.rec.service.beacon.BeaconService;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletionStage;

public class BeaconResourceImpl implements BeaconResource {

  @Inject
  BeaconService beaconService;

  @Override
  public CompletionStage<Response> beacon(BeaconDTO payload) {
    Beacon beacon = Beacon.from(payload);
    return beaconService.process(beacon).thenApply(DataResponse::ok);
  }


}
