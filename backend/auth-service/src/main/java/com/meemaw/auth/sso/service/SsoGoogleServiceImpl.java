package com.meemaw.auth.sso.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.meemaw.auth.sso.model.SsoSocialLogin;
import com.meemaw.auth.sso.model.dto.IdTokenDTO;
import com.meemaw.shared.rest.response.Boom;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletionStage;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Slf4j
public class SsoGoogleServiceImpl implements SsoGoogleService {

  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final Collection<String> SCOPES = Collections.singletonList("email");
  private static final String USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/tokeninfo";

  @ConfigProperty(name = "google.client.id")
  String GOOGLE_CLIENT_ID;

  @ConfigProperty(name = "google.client.secret")
  String GOOGLE_CLIENT_SECRET;

  @Inject
  SsoService ssoService;

  private GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow() {
    return new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT,
        JSON_FACTORY,
        GOOGLE_CLIENT_ID,
        GOOGLE_CLIENT_SECRET,
        SCOPES
    ).build();
  }


  @Override
  public String signin(String dest, String redirectURI) {
    GoogleAuthorizationCodeFlow flow = googleAuthorizationCodeFlow();
    String state = URLEncoder.encode(dest, StandardCharsets.UTF_8);
    return flow.newAuthorizationUrl()
        .setRedirectUri(redirectURI)
        .setState(state)
        .build();
  }

  @Override
  public CompletionStage<SsoSocialLogin> oauth2callback(String dest, String code,
      String redirectURI) {
    GoogleAuthorizationCodeFlow flow = googleAuthorizationCodeFlow();
    try {
      TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectURI)
          .execute();

      Credential credential = flow.createAndStoreCredential(tokenResponse, null);
      HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
      GenericUrl url = new GenericUrl(USER_INFO_ENDPOINT);
      HttpRequest request = requestFactory.buildGetRequest(url);

      // TODO: oh Google, still using Future ...
      String jsonIdentity = request.execute().parseAsString();
      // TODO: reuse ObjectMapper
      IdTokenDTO idTokenDTO = new ObjectMapper().readValue(jsonIdentity, IdTokenDTO.class);

      String email = idTokenDTO.getEmail();
      // TODO: read from env
      String Location = "http://localhost:3000" + URLDecoder.decode(dest, StandardCharsets.UTF_8);

      log.info("Google oauth2callback redirecting {} to {}", email, Location);
      return ssoService.socialLogin(email)
          .thenApply(sessionId -> new SsoSocialLogin(sessionId, Location));
    } catch (IOException ex) {
      if (ex instanceof TokenResponseException) {
        TokenResponseException tokenResponseException = (TokenResponseException) ex;
        String errorDescription = tokenResponseException.getDetails().getErrorDescription();
        String error = tokenResponseException.getDetails().getError();
        if (error.equals("invalid_grant")) {
          String message = String.format("%s. %s", error, errorDescription);
          throw Boom.badRequest().message(message).exception();
        }
      }

      log.error("Unhandled Google oauth2callback error dest={} code={}", dest, code, ex);
      throw Boom.serverError().message(ex.getMessage()).exception();
    }

  }
}
