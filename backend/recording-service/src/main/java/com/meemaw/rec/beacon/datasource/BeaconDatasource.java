package com.meemaw.rec.beacon.datasource;

import com.meemaw.rec.beacon.model.Beacon;
import java.util.concurrent.CompletionStage;

public interface BeaconDatasource {

  CompletionStage<Void> store(Beacon beacon);
}
