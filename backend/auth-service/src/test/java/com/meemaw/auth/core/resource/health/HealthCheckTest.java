package com.meemaw.auth.resource.health;


import static com.meemaw.test.matchers.SameJSON.sameJson;
import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

public class HealthCheckTest {

  @Test
  public void health() {
    given()
        .when().post("/health")
        .then()
        .statusCode(200)
        .body(sameJson(
            "{\"status\":\"UP\",\"checks\":[{\"name\":\"LivenessHealthCheck\",\"status\":\"UP\"},{\"name\":\"ReadinessHealthCheck\",\"status\":\"UP\"}]}"));
  }

  @Test
  public void liveness() {
    given()
        .when().post("/health/live")
        .then()
        .statusCode(200)
        .body(sameJson(
            "{\"status\":\"UP\",\"checks\":[{\"name\":\"LivenessHealthCheck\",\"status\":\"UP\"}]}"));
  }

  @Test
  public void readiness() {
    given()
        .when().post("/health/ready")
        .then()
        .statusCode(200)
        .body(sameJson(
            "{\"status\":\"UP\",\"checks\":[{\"name\":\"ReadinessHealthCheck\",\"status\":\"UP\"}]}"));
  }
}
