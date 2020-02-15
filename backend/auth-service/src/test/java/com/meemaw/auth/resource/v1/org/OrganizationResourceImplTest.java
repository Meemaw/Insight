package com.meemaw.auth.resource.v1.org;

import static com.meemaw.test.matchers.SameJSON.sameJson;
import static io.restassured.RestAssured.given;

import com.meemaw.auth.model.sso.SsoSession;
import com.meemaw.auth.resource.v1.signup.SignupResourceImplTest;
import com.meemaw.auth.resource.v1.sso.SsoResourceImplTest;
import com.meemaw.test.testconainers.Postgres;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Postgres
@QuarkusTest
@Tag("integration")
public class OrganizationResourceImplTest {

  @Inject
  MockMailbox mailbox;

  @BeforeEach
  void init() {
    mailbox.clear();
  }

  private static String sessionId;

  public String getSessionId() {
    if (sessionId == null) {
      String email = "org_invite_test@gmail.com";
      String password = "org_invite_test_password";

      SignupResourceImplTest.signup(mailbox, email, password);
      sessionId = SsoResourceImplTest.login(email, password);
    }

    return sessionId;
  }


  @Test
  public void invite_should_fail_when_invalid_contentType() {
    given()
        .when()
        .contentType(MediaType.TEXT_PLAIN)
        .post(OrganizationResource.PATH + "/invite")
        .then()
        .statusCode(415)
        .body(sameJson(
            "{\"error\":{\"statusCode\":415,\"reason\":\"Unsupported Media Type\",\"message\":\"Media type not supported.\"}}"));
  }

  @Test
  public void invite_should_fail_when_not_authenticated() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .post(OrganizationResource.PATH + "/invite")
        .then()
        .statusCode(401)
        .body(sameJson(
            "{\"error\":{\"statusCode\":401,\"reason\":\"Unauthorized\",\"message\":\"Unauthorized\"}}"));
  }

  @Test
  public void invite_should_fail_when_no_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .post(OrganizationResource.PATH + "/invite")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"arg0\":\"Payload is required\"}}}"));
  }

  @Test
  public void invite_should_fail_when_empty_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body("{}")
        .post(OrganizationResource.PATH + "/invite")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"role\":\"Required\",\"email\":\"Required\"}}}"));
  }

  @Test
  public void invite_should_fail_when_invalid_role() throws URISyntaxException, IOException {
    String payload = Files.readString(Path.of(getClass().getResource(
        "/org/invite/invalidFirst.json").toURI()));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(payload)
        .post(OrganizationResource.PATH + "/invite")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Cannot deserialize value of type `com.meemaw.auth.model.user.UserRole` from String \\\"HA\\\": not one of the values accepted for Enum class: [STANDARD, ADMIN]\"}}"));
  }

  @Test
  public void invite_should_fail_when_invalid_email() throws URISyntaxException, IOException {
    String payload = Files.readString(Path.of(getClass().getResource(
        "/org/invite/invalidSecond.json").toURI()));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(payload)
        .post(OrganizationResource.PATH + "/invite")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"email\":\"must be a well-formed email address\"}}}"));
  }

  @Test
  public void invite_flow_should_succeed_on_valid_payload()
      throws URISyntaxException, IOException {
    String payload = Files.readString(Path.of(getClass().getResource(
        "/org/invite/valid.json").toURI()));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(payload)
        .post(OrganizationResource.PATH + "/invite")
        .then()
        .statusCode(201);

    // creating same invite twice should fail with 409 Conflict
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .cookie(SsoSession.COOKIE_NAME, getSessionId())
        .body(payload)
        .post(OrganizationResource.PATH + "/invite")
        .then()
        .statusCode(409)
        .body(sameJson(
            "{\"error\":{\"statusCode\":409,\"reason\":\"Conflict\",\"message\":\"User has already been invited\"}}"));
  }

}
