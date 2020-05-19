package com.meemaw.auth.sso.datasource;

import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class HazelcastProvider {

  @Getter private HazelcastInstance instance;

  public void init(@Observes StartupEvent event) {
    log.info("Initializing ...");
    instance = Hazelcast.newHazelcastInstance(new XmlConfigBuilder().build());
  }

  public void shutdown(@Observes ShutdownEvent event) {
    log.info("Shutting down ...");
    instance.shutdown();
  }
}
