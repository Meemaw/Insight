package com.meemaw.test.setup;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.auth.signup.model.dto.SignUpRequestDTO;
import com.meemaw.auth.signup.resource.v1.SignUpResource;
import com.meemaw.auth.sso.model.SsoSession;
import com.meemaw.auth.sso.resource.v1.SsoResource;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.MediaType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public final class SsoTestSetupUtils {

  private SsoTestSetupUtils() {}

  public static SignUpRequestDTO signUpRequestMock(String email, String password) {
    return new SignUpRequestDTO(email, password, "Marko Novak", "Insight", null);
  }

  public static String signUpAndLogin(
      MockMailbox mockMailbox, ObjectMapper objectMapper, String email, String password)
      throws JsonProcessingException {
    return signUpAndLogin(mockMailbox, objectMapper, signUpRequestMock(email, password));
  }

  /**
   * Sign up user and return newly created SessionId.
   *
   * @param mailbox mock mailbox
   * @param objectMapper jackson object mapper
   * @param signUpRequestDTO sign up request data
   * @return String SessionId
   * @throws JsonProcessingException
   */
  public static String signUpAndLogin(
      MockMailbox mailbox, ObjectMapper objectMapper, SignUpRequestDTO signUpRequestDTO)
      throws JsonProcessingException {

    given()
        .when()
        .contentType(MediaType.APPLICATION_JSON)
        .body(objectMapper.writeValueAsString(signUpRequestDTO))
        .post(SignUpResource.PATH)
        .then()
        .statusCode(204);

    Response response =
        given()
            .when()
            .get(parseLink(mailbox.getMessagesSentTo(signUpRequestDTO.getEmail()).get(0)));

    response.then().statusCode(204).cookie(SsoSession.COOKIE_NAME);
    return extractSessionCookie(response).getValue();
  }

  public static String parseLink(Mail mail) {
    Document htmlDocument = Jsoup.parse(mail.getHtml());
    Elements link = htmlDocument.select("a");
    return link.attr("href");
  }

  public static String parseCompleteSignUpToken(Mail completeSignUpMail) {
    String signUpCompleteURL = parseLink(completeSignUpMail);
    Matcher tokenMatcher = Pattern.compile("^.*token=(.*)$").matcher(signUpCompleteURL);
    tokenMatcher.matches();
    return tokenMatcher.group(1);
  }

  /**
   * Log in with provided credentials.
   *
   * @param email address
   * @param password from the user
   * @return String SessionID
   */
  public static String login(String email, String password) {
    Response response =
        given()
            .when()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("email", email)
            .param("password", password)
            .post(SsoResource.PATH + "/login");

    response.then().statusCode(204).cookie(SsoSession.COOKIE_NAME);
    return extractSessionCookie(response).getValue();
  }

  public static Cookie extractSessionCookie(Response response) {
    return response.getDetailedCookie(SsoSession.COOKIE_NAME);
  }
}
