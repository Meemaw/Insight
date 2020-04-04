package com.meemaw.rec.beacon.service;

import com.meemaw.rec.beacon.datasource.BeaconDatasource;
import com.meemaw.rec.beacon.model.Beacon;
import com.meemaw.shared.rest.exception.DatabaseException;
import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@Slf4j
public class BeaconService {

  @Inject
  BeaconDatasource beaconDatasource;


  public CompletionStage<Void> process(Beacon beacon) {
    return beaconDatasource.store(beacon);
  }
}
