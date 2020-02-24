package com.meemaw.auth.sso.resource.v1.google;

import com.meemaw.auth.sso.model.SsoSession;
import com.meemaw.auth.sso.service.google.SsoGoogleService;
import java.net.URI;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SsoGoogleResourceImpl implements SsoGoogleResource {

  @Inject
  SsoGoogleService ssoGoogleService;

  @Context
  UriInfo info;

  private String getRedirectUri() {
    // TODO: fix this; info.getBaseUri() will fail for tests
    return "http://localhost:8080/" + SsoGoogleResource.PATH + "/oauth2callback";
  }

  @Override
  public Response signin(String dest) {
    URI Location = ssoGoogleService.buildAuthorizationURI(dest, getRedirectUri());
    return Response.status(Status.FOUND).header("Location", Location).build();
  }

  @Override
  public CompletionStage<Response> oauth2callback(String state, String authorizationCode) {
    return ssoGoogleService.oauth2callback(state, authorizationCode, getRedirectUri())
        .thenApply(ssoSocialLogin -> {
          String Location = ssoSocialLogin.getLocation();
          String SessionId = ssoSocialLogin.getSessionId();
          return Response.status(Status.FOUND).header("Location", Location)
              .cookie(SsoSession.cookie(SessionId)).build();
        });
  }
}
