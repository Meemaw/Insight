package com.meemaw.auth.resource.v1.signup;

import static com.meemaw.test.matchers.SameJSON.sameJson;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.meemaw.auth.resource.v1.sso.SsoResourceImplTest;
import com.meemaw.test.rest.mappers.JacksonMapper;
import com.meemaw.test.testconainers.Postgres;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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


@Postgres
@QuarkusTest
@Tag("integration")
public class SignupResourceImplTest {

  @Inject
  MockMailbox mailbox;

  @BeforeEach
  void init() {
    mailbox.clear();
  }

  @Test
  public void signup_should_fail_when_invalid_contentType() {
    given()
        .when()
        .contentType(MediaType.TEXT_PLAIN).post(SignupResource.PATH)
        .then()
        .statusCode(415)
        .body(sameJson(
            "{\"error\":{\"statusCode\":415,\"reason\":\"Unsupported Media Type\",\"message\":\"Media type not supported.\"}}"));
  }

  @Test
  public void signup_should_fail_when_no_email() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .post(SignupResource.PATH)
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"arg0\":\"Email is required\"}}}"));
  }

  @Test
  public void signup_should_fail_when_invalid_email() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("email", "random")
        .post(SignupResource.PATH)
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"arg0\":\"must be a well-formed email address\"}}}"));
  }

  @Test
  public void signupVerify_should_fail_when_invalid_contentType() {
    given()
        .when()
        .contentType(MediaType.TEXT_PLAIN).post(SignupResource.PATH + "/verify")
        .then()
        .statusCode(415)
        .body(sameJson(
            "{\"error\":{\"statusCode\":415,\"reason\":\"Unsupported Media Type\",\"message\":\"Media type not supported.\"}}"));
  }

  @Test
  public void signupVerify_should_fail_when_no_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .post(SignupResource.PATH + "/verify")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"arg0\":\"Payload is required\"}}}"));
  }

  @Test
  public void signupVerify_should_fail_when_empty_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON).body("{}")
        .post(SignupResource.PATH + "/verify")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"org\":\"Required\",\"email\":\"Required\",\"token\":\"Required\"}}}"));
  }

  @Test
  public void signupVerify_should_fail_when_invalid_payload_1()
      throws URISyntaxException, IOException {
    String payload = Files.readString(Path.of(getClass().getResource(
        "/signup/verify/invalidFirst.json").toURI()));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON).body(payload)
        .post(SignupResource.PATH + "/verify")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Cannot deserialize value of type `java.util.UUID` from String \\\"notReallyAnUuid\\\": UUID has to be represented by standard 36-char representation\"}}"));
  }

  @Test
  public void signupVerify_should_fail_when_invalid_payload_2()
      throws URISyntaxException, IOException {
    String payload = Files.readString(Path.of(getClass().getResource(
        "/signup/verify/invalidSecond.json").toURI()));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON).body(payload)
        .post(SignupResource.PATH + "/verify")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"org\":\"Required\",\"email\":\"must be a well-formed email address\"}}}"));
  }

  @Test
  public void signupVerify_should_return_false_when_missing_payload()
      throws URISyntaxException, IOException {
    String payload = Files.readString(Path.of(getClass().getResource(
        "/signup/verify/missing.json").toURI()));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON).body(payload)
        .post(SignupResource.PATH + "/verify")
        .then()
        .statusCode(200)
        .body(sameJson("{\"data\":false}"));
  }

  @Test
  public void signupComplete_should_fail_when_invalid_contentType() {
    given()
        .when()
        .contentType(MediaType.TEXT_PLAIN).post(SignupResource.PATH + "/complete")
        .then()
        .statusCode(415)
        .body(sameJson(
            "{\"error\":{\"statusCode\":415,\"reason\":\"Unsupported Media Type\",\"message\":\"Media type not supported.\"}}"));
  }

  @Test
  public void signupComplete_should_fail_when_no_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .post(SignupResource.PATH + "/complete")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"arg0\":\"Payload is required\"}}}"));
  }

  @Test
  public void signupComplete_should_fail_when_empty_payload() {
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON).body("{}")
        .post(SignupResource.PATH + "/complete")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"password\":\"Required\",\"org\":\"Required\",\"email\":\"Required\",\"token\":\"Required\"}}}"));
  }

  @Test
  public void signupComplete_should_fail_when_invalid_payload_2()
      throws URISyntaxException, IOException {
    String payload = Files.readString(Path.of(getClass().getResource(
        "/signup/complete/invalidSecond.json").toURI()));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON).body(payload)
        .post(SignupResource.PATH + "/complete")
        .then()
        .statusCode(400)
        .body(sameJson(
            "{\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Validation Error\",\"errors\":{\"password\":\"Password must be at least 8 characters long\",\"org\":\"Required\",\"email\":\"must be a well-formed email address\"}}}"));
  }

  @Test
  public void signupComplete_should_fail_when_missing_payload()
      throws URISyntaxException, IOException {
    String payload = Files.readString(Path.of(getClass().getResource(
        "/signup/complete/missing.json").toURI()));

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON).body(payload)
        .post(SignupResource.PATH + "/complete")
        .then()
        .statusCode(400)
        .body(sameJson(
            " {\"error\":{\"statusCode\":400,\"reason\":\"Bad Request\",\"message\":\"Signup request does not exist.\"}}"));
  }

  @Test
  public void signup_flow_should_succeed_on_valid_email() {
    String signupEmail = "test@gmail.com";
    String signupPassword = "superDuperPassword";

    signup(mailbox, signupEmail, signupPassword);

    // should be able to login with the newly created account
    SsoResourceImplTest.login(signupEmail, signupPassword);
  }

  public static void signup(MockMailbox mailbox, String signupEmail, String signupPassword) {
    given()
        .when()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("email", signupEmail)
        .post(SignupResource.PATH)
        .then()
        .statusCode(200);

    List<Mail> sent = mailbox.getMessagesSentTo(signupEmail);
    assertEquals(1, sent.size());
    Mail actual = sent.get(0);
    assertEquals("Insight Support <support@insight.com>", actual.getFrom());

    Document doc = Jsoup.parse(actual.getHtml());
    Elements link = doc.select("a");
    String signupCompleteUrl = link.attr("href");

    Matcher orgMatcher = Pattern.compile("^.*orgId=(.*)&.*$").matcher(signupCompleteUrl);
    orgMatcher.matches();
    String orgId = orgMatcher.group(1);

    Matcher tokenMatcher = Pattern.compile("^.*token=(.*)$").matcher(signupCompleteUrl);
    tokenMatcher.matches();
    String token = tokenMatcher.group(1);

    // verify that the SignupRequest is still valid
    ObjectNode node = JacksonMapper.get().createObjectNode();
    node.put("email", signupEmail);
    node.put("org", orgId);
    node.put("token", token);

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(node)
        .post(SignupResource.PATH + "/verify")
        .then()
        .statusCode(200)
        .body(sameJson("{\"data\":true}"));

    node.put("password", signupPassword);

    // complete the signup
    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(node)
        .post(SignupResource.PATH + "/complete")
        .then()
        .statusCode(200)
        .body(sameJson("{\"data\":true}"));
  }

}
