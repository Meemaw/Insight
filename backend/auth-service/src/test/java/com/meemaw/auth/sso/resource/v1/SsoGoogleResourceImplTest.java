package com.meemaw.auth.sso.resource.v1;


import static com.meemaw.test.matchers.SameJSON.sameJson;
import static io.restassured.RestAssured.given;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.newConfig;

import io.quarkus.test.junit.QuarkusTest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Tag("integration")
public class SsoGoogleResourceImplTest {

  @ConfigProperty(name = "google.client.id")
  String GOOGLE_CLIENT_ID;

  @Test
  public void google_signin_should_fail_when_no_dest() {
    given()
        .when()
        .get(SsoGoogleResource.PATH + "/signin")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"arg0\":\"dest is required\"}}}"));
  }

  @Test
  public void google_signin_should_start_flow_by_redirecting_to_google() {
    String expectedLocation =
        "https://accounts.google.com/o/oauth2/auth?client_id=" + GOOGLE_CLIENT_ID
            + "&redirect_uri=http://localhost:8080/v1/sso/google/oauth2callback&response_type=code&scope=email&state=%252Ftest";

    given()
        .config(newConfig().redirect(redirectConfig().followRedirects(false)))
        .when()
        .queryParam("dest", "/test")
        .get(SsoGoogleResource.PATH + "/signin")
        .then()
        .statusCode(302)
        .header("Location", expectedLocation);
  }

  @Test
  public void google_oauth2callback_should_fail_when_no_params() {
    given()
        .when()
        .get(SsoGoogleResource.PATH + "/oauth2callback")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"arg1\":\"code is required\",\"arg0\":\"state is required\"}}}"));
  }

  @Test
  public void google_oauth2callback_should_fail_on_random_code() {
    given()
        .when()
        .queryParam("code", "random")
        .queryParam("state", URLEncoder.encode("/test", StandardCharsets.UTF_8))
        .get(SsoGoogleResource.PATH + "/oauth2callback")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"invalid_grant. Malformed auth code.\"}}"));
  }

  @Test
  public void google_oauth2callback_should_fail_on_expired_code() {
    given()
        .when()
        .queryParam("code",
            "4/wwF1aA6SPPRdiJdy95vNLmeFt5237v5juu86VqdJxyR_3VruynuXyXUbFFhtmdGd1jApNM3P3vr8fgGpey-NryM")
        .queryParam("state", URLEncoder.encode("/test", StandardCharsets.UTF_8))
        .get(SsoGoogleResource.PATH + "/oauth2callback")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"invalid_grant. Bad Request\"}}"));
  }

}
