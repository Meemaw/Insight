package com.meemaw.test.testconainers.service;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.HashMap;
import java.util.Map;

public class AuthApiResource implements QuarkusTestResourceLifecycleManager {

  private static final AuthApiTestContainer AUTH_API = AuthApiTestContainer.newInstance();

  public static AuthApiTestContainer getInstance() {
    return AUTH_API;
  }

  @Override
  public Map<String, String> start() {
    System.out.println("Starting auth api!");
    getInstance().withLogConsumer(System.out::println).start();
    getInstance().start();
    System.out.println("Started auth api!");
    return new HashMap<>();
  }

  @Override
  public void stop() {
    getInstance().stop();
  }
}
