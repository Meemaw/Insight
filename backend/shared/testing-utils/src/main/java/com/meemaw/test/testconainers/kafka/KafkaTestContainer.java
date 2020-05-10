package com.meemaw.test.testconainers.kafka;

import org.testcontainers.containers.KafkaContainer;

public class KafkaTestContainer extends KafkaContainer {

  private static final String CONFLUENT_PLATFORM_VERSION = "5.5.0";

  public KafkaTestContainer() {
    super(CONFLUENT_PLATFORM_VERSION);
  }
}
