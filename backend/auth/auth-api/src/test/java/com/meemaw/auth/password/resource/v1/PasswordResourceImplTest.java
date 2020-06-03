package com.meemaw.auth.password.resource.v1;

import static com.meemaw.test.matchers.SameJSON.sameJson;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.auth.password.model.dto.PasswordForgotRequestDTO;
import com.meemaw.auth.password.model.dto.PasswordResetRequestDTO;
import com.meemaw.auth.sso.model.SsoSession;
import com.meemaw.auth.sso.resource.v1.SsoResource;
import com.meemaw.test.setup.SsoTestSetupUtils;
import com.meemaw.test.testconainers.pg.PostgresTestResource;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@QuarkusTestResource(PostgresTestResource.class)
@QuarkusTest
@Tag("integration")
public class PasswordResourceImplTest {

  @Inject MockMailbox mailbox;

  @Inject ObjectMapper objectMapper;

  @BeforeEach
  void init() {
    mailbox.clear();
  }

  @Test
  public void forgot_should_fail_when_invalid_contentType() {
    given()
        .when()
        .contentType(MediaType.TEXT_PLAIN)
        .post(PasswordResource.PATH + "/forgot")
        .then()
        .statusCode(415)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":415,\"reason\":\"Unsupported Media Type\",\"message\":\"Media type not supported.\"}}"));
  }

  @Test
  public void forgot_should_fail_when_no_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .post(PasswordResource.PATH + "/forgot")
        .then()
        .statusCode(400)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"body\":\"Required\"}}}"));
  }

  @Test
  public void forgot_should_fail_when_empty_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body("{}")
        .post(PasswordResource.PATH + "/forgot")
        .then()
        .statusCode(400)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"email\":\"Required\"}}}"));
  }

  @Test
  public void forgot_should_fail_when_empty_email() throws JsonProcessingException {
    String payload = objectMapper.writeValueAsString(new PasswordForgotRequestDTO(""));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .post(PasswordResource.PATH + "/forgot")
        .then()
        .statusCode(400)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"email\":\"Required\"}}}"));
  }

  @Test
  public void forgot_should_fail_on_invalid_email() throws JsonProcessingException {
    String payload = objectMapper.writeValueAsString(new PasswordForgotRequestDTO("notEmail"));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .post(PasswordResource.PATH + "/forgot")
        .then()
        .statusCode(400)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"email\":\"must be a well-formed email address\"}}}"));
  }

  @Test
  public void forgot_should_fail_on_missing_email() throws JsonProcessingException {
    String payload =
        objectMapper.writeValueAsString(new PasswordForgotRequestDTO("missing@test.com"));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .post(PasswordResource.PATH + "/forgot")
        .then()
        .statusCode(404)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":404,\"reason\":\"Not Found\",\"message\":\"User not found\"}}"));
  }

  @Test
  public void forgot_should_send_email_on_existing_user() throws JsonProcessingException {
    String email = "test-forgot-password-flow@gmail.com";
    String password = "superHardPassword";
    SsoTestSetupUtils.signUpAndLogin(mailbox, objectMapper, email, password);
    PasswordResourceImplTest.passwordForgot(email, objectMapper);
    // can trigger the forgot flow multiple times!!
    PasswordResourceImplTest.passwordForgot(email, objectMapper);
  }

  /**
   * Initializes a password forgot request flow and throws is it is not successful.
   *
   * @param email address
   * @param objectMapper object mapper
   * @return password forgot request response
   * @throws JsonProcessingException if failed to serialize password forgot request
   */
  public static Response passwordForgot(String email, ObjectMapper objectMapper)
      throws JsonProcessingException {
    String payload = objectMapper.writeValueAsString(new PasswordForgotRequestDTO(email));

    Response response =
        given()
            .when()
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .post(PasswordResource.PATH + "/forgot");

    response.then().statusCode(201).body(sameJson("{\"data\":true}"));

    return response;
  }

  @Test
  public void reset_should_fail_when_invalid_contentType() {
    given()
        .when()
        .contentType(MediaType.TEXT_PLAIN)
        .post(PasswordResource.PATH + "/reset")
        .then()
        .statusCode(415)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":415,\"reason\":\"Unsupported Media Type\",\"message\":\"Media type not supported.\"}}"));
  }

  @Test
  public void reset_should_fail_when_no_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .post(PasswordResource.PATH + "/reset")
        .then()
        .statusCode(400)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"body\":\"Required\"}}}"));
  }

  @Test
  public void reset_should_fail_when_empty_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body("{}")
        .post(PasswordResource.PATH + "/reset")
        .then()
        .statusCode(400)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"password\":\"Required\",\"token\":\"Required\"}}}"));
  }

  @Test
  public void reset_should_fail_when_invalid_payload() throws JsonProcessingException {
    String payload =
        objectMapper.writeValueAsString(new PasswordResetRequestDTO(UUID.randomUUID(), "pass"));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .post(PasswordResource.PATH + "/reset")
        .then()
        .statusCode(400)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"password\":\"Password must be at least 8 characters long\"}}}"));
  }

  @Test
  public void reset_should_fail_when_missing_payload() throws JsonProcessingException {
    String payload =
        objectMapper.writeValueAsString(
            new PasswordResetRequestDTO(UUID.randomUUID(), "passLongEnough"));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(payload)
        .post(PasswordResource.PATH + "/reset")
        .then()
        .statusCode(404)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":404,\"reason\":\"Not Found\",\"message\":\"Password reset request not found\"}}"));
  }

  @Test
  public void reset_exists_should_fail_when_no_payload() {
    given()
        .when()
        .get(PasswordResource.PATH + "/reset/exists")
        .then()
        .statusCode(400)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"token\":\"Required\"}}}"));
  }

  @Test
  public void reset_exists_should_return_false_when_missing_params() {
    given()
        .when()
        .queryParam("email", "test@gmail.com")
        .queryParam("token", "4f113105-94d9-4470-8621-0e633fa4697")
        .get(PasswordResource.PATH + "/reset/exists")
        .then()
        .statusCode(200)
        .body(sameJson("{\"data\":false}"));
  }

  @Test
  public void reset_flow_should_succeed_after_signup() throws JsonProcessingException {
    String signUpEmail = "reset-password-flow@gmail.com";
    String oldPassword = "superHardPassword";
    SsoTestSetupUtils.signUpAndLogin(mailbox, objectMapper, signUpEmail, oldPassword);
    PasswordResourceImplTest.passwordForgot(signUpEmail, objectMapper);

    // login with "oldPassword" should succeed
    given()
        .when()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("email", signUpEmail)
        .param("password", oldPassword)
        .post(SsoResource.PATH + "/login")
        .then()
        .statusCode(204);

    List<Mail> sent = mailbox.getMessagesSentTo(signUpEmail);
    assertEquals(2, sent.size());
    Mail actual = sent.get(1);
    assertEquals("Insight Support <support@insight.com>", actual.getFrom());

    Document doc = Jsoup.parse(actual.getHtml());
    Elements link = doc.select("a");
    String passwordForgotLink = link.attr("href");

    Matcher tokenMatcher = Pattern.compile("^.*token=(.*)$").matcher(passwordForgotLink);
    tokenMatcher.matches();
    String token = tokenMatcher.group(1);

    assertEquals(passwordForgotLink, "http://localhost:8081/password-reset?token=" + token);

    // reset request should exist
    given()
        .when()
        .queryParam("token", token)
        .get(PasswordResource.PATH + "/reset/exists")
        .then()
        .statusCode(200)
        .body(sameJson("{\"data\":true}"));

    String newPassword = "superDuperNewFancyPassword";
    String resetPasswordPayload =
        objectMapper.writeValueAsString(
            new PasswordResetRequestDTO(UUID.fromString(token), newPassword));

    // successful reset should login the user
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(resetPasswordPayload)
        .post(PasswordResource.PATH + "/reset")
        .then()
        .statusCode(204)
        .cookie(SsoSession.COOKIE_NAME);

    // login with "oldPassword" should fail
    given()
        .when()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("email", signUpEmail)
        .param("password", oldPassword)
        .post(SsoResource.PATH + "/login")
        .then()
        .statusCode(400)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Invalid email or password\"}}"));

    // login with "newPassword" should succeed
    given()
        .when()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("email", signUpEmail)
        .param("password", newPassword)
        .post(SsoResource.PATH + "/login")
        .then()
        .statusCode(204)
        .cookie(SsoSession.COOKIE_NAME);

    // trying to do the rest with same params again should fail
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(resetPasswordPayload)
        .post(PasswordResource.PATH + "/reset")
        .then()
        .statusCode(404)
        .body(
            sameJson(
                "{\"error\":{\"statusCode\":404,\"reason\":\"Not Found\",\"message\":\"Password reset request not found\"}}"));
  }
}
