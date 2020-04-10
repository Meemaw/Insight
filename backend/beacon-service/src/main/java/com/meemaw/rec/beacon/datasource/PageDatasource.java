package com.meemaw.rec.beacon.datasource;

import io.smallrye.mutiny.Uni;
import java.util.UUID;

public interface PageDatasource {

  Uni<Boolean> pageExists(UUID sessionID, UUID uid, UUID pageID);
}
