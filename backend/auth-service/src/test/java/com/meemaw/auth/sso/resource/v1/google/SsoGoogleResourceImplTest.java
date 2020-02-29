package com.meemaw.auth.sso.resource.v1.google;


import static com.meemaw.test.matchers.SameJSON.sameJson;
import static io.restassured.RestAssured.given;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.newConfig;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
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
    String expectedLocationBase =
        "https://accounts.google.com/o/oauth2/auth?client_id=" + GOOGLE_CLIENT_ID
            + "&redirect_uri=http%3A%2F%2Flocalhost%3A8081%2Fv1%2Fsso%2Fgoogle%2Foauth2callback&response_type=code&scope=openid+email+profile&state=";

    Response response = given()
        .config(newConfig().redirect(redirectConfig().followRedirects(false)))
        .when()
        .queryParam("dest", "/test")
        .get(SsoGoogleResource.PATH + "/signin");

    response
        .then()
        .statusCode(302)
        .header("Location", startsWith(expectedLocationBase));

    String state = response.header("Location").replace(expectedLocationBase, "");
    String destination = state.substring(26);

    assertEquals("%2Ftest", destination);
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
    String state = URLEncoder.encode("/test", StandardCharsets.UTF_8);
    given()
        .when()
        .queryParam("code", "random")
        .queryParam("state", state)
        .cookie("state", state)
        .get(SsoGoogleResource.PATH + "/oauth2callback")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"invalid_grant. Malformed auth code.\"}}"));
  }

  @Test
  public void google_oauth2callback_should_fail_on_expired_code() {
    String state = URLEncoder.encode("/test", StandardCharsets.UTF_8);

    given()
        .when()
        .queryParam("code",
            "4/wwF1aA6SPPRdiJdy95vNLmeFt5237v5juu86VqdJxyR_3VruynuXyXUbFFhtmdGd1jApNM3P3vr8fgGpey-NryM")
        .queryParam("state", state)
        .cookie("state", state)
        .get(SsoGoogleResource.PATH + "/oauth2callback")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"invalid_grant. Bad Request\"}}"));
  }

  @Test
  public void google_oauth2callback_should_fail_on_state_cookie() {
    String state = URLEncoder.encode("/test", StandardCharsets.UTF_8);

    given()
        .when()
        .queryParam("code",
            "4/wwF1aA6SPPRdiJdy95vNLmeFt5237v5juu86VqdJxyR_3VruynuXyXUbFFhtmdGd1jApNM3P3vr8fgGpey-NryM")
        .queryParam("state", state)
        .get(SsoGoogleResource.PATH + "/oauth2callback")
        .then()
        .statusCode(401)
        .body(sameJson(
            "{\"error\":{\"statusCode\":401,\"reason\":\"Unauthorized\",\"message\":\"Invalid state parameter\"}}"));
  }

}
