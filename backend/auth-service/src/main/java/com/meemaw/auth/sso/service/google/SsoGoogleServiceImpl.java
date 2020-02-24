package com.meemaw.auth.sso.service.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meemaw.auth.sso.model.SsoSocialLogin;
import com.meemaw.auth.sso.model.google.GoogleAccessTokenClaims;
import com.meemaw.auth.sso.model.google.GoogleErrorResponse;
import com.meemaw.auth.sso.model.google.GoogleTokenResponse;
import com.meemaw.auth.sso.service.SsoService;
import com.meemaw.shared.rest.response.Boom;
import io.vertx.axle.core.Vertx;
import io.vertx.axle.core.buffer.Buffer;
import io.vertx.axle.ext.web.client.HttpResponse;
import io.vertx.axle.ext.web.client.WebClient;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletionStage;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
public class SsoGoogleServiceImpl implements SsoGoogleService {


  private static final Collection<String> SCOPE_LIST = Collections.singletonList("email");
  private static final String SCOPES = String.join(",", SCOPE_LIST);
  private static final String USER_INFO_SERVER_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo";
  private static final String AUTHORIZATION_SERVER_URL = "https://accounts.google.com/o/oauth2/auth";
  private static final String TOKEN_SERVER_URL = "https://oauth2.googleapis.com/token";

  @ConfigProperty(name = "google.client.id")
  String GOOGLE_CLIENT_ID;

  @ConfigProperty(name = "google.client.secret")
  String GOOGLE_CLIENT_SECRET;

  @Inject
  ObjectMapper objectMapper;

  @Inject
  SsoService ssoService;

  @Inject
  Vertx vertx;

  private WebClient webClient;

  @PostConstruct
  void initialize() {
    webClient = WebClient.create(vertx);
  }

  @Override
  public URI buildAuthorizationURI(String dest, String redirectURI) {
    return UriBuilder.fromUri(AUTHORIZATION_SERVER_URL)
        .queryParam("client_id", GOOGLE_CLIENT_ID)
        .queryParam("redirect_uri", redirectURI)
        .queryParam("response_type", "code")
        .queryParam("scope", SCOPES)
        .queryParam("state", dest)
        .build();
  }

  @Override
  public CompletionStage<SsoSocialLogin> oauth2callback(String dest,
      String authorizationCode,
      String redirectURI) {

    return webClient.postAbs(TOKEN_SERVER_URL)
        .addQueryParam("grant_type", "authorization_code")
        .addQueryParam("code", authorizationCode)
        .addQueryParam("client_id", GOOGLE_CLIENT_ID)
        .addQueryParam("client_secret", GOOGLE_CLIENT_SECRET)
        .addQueryParam("redirect_uri", redirectURI)
        .putHeader("Content-Length", "0")
        .send()
        .thenApply(this::parseTokenResponse)
        .thenCompose(this::exchangeToken)
        .thenCompose(claims -> {
          String email = claims.getEmail();
          String Location =
              "http://localhost:3000" + URLDecoder.decode(dest, StandardCharsets.UTF_8);

          log.info("Google oauth2callback redirecting {} to {}", email, Location);
          return ssoService.socialLogin(email)
              .thenApply(sessionId -> new SsoSocialLogin(sessionId, Location));
        });
  }

  private GoogleTokenResponse parseTokenResponse(HttpResponse<Buffer> response) {
    return handleGoogleResponse(response, GoogleTokenResponse.class);

  }

  private CompletionStage<GoogleAccessTokenClaims> exchangeToken(GoogleTokenResponse token) {
    return webClient.getAbs(USER_INFO_SERVER_URL)
        .addQueryParam("access_token", token.getAccessToken())
        .send()
        .thenApply(response -> handleGoogleResponse(response, GoogleAccessTokenClaims.class));
  }

  private <T> T handleGoogleResponse(HttpResponse<Buffer> response, Class<T> clazz) {
    String jsonPayload = response.bodyAsString();
    int statusCode = response.statusCode();

    try {
      if (statusCode != 200) {
        GoogleErrorResponse errorResponse = objectMapper
            .readValue(jsonPayload, GoogleErrorResponse.class);
        String errorDescription = errorResponse.getErrorDescription();
        String error = errorResponse.getError();
        String message = String.format("%s. %s", error, errorDescription);
        throw Boom.status(statusCode).message(message).exception();
      }

      return objectMapper.readValue(jsonPayload, clazz);
    } catch (JsonProcessingException ex) {
      log.error("Failed to parse google access token claims", ex);
      throw Boom.serverError().message(ex.getMessage()).exception();
    }
  }

}
