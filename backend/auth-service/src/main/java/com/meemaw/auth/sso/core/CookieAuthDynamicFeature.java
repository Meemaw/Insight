package com.meemaw.auth.sso.core;

import com.meemaw.auth.sso.datasource.SsoDatasource;
import com.meemaw.auth.sso.model.SsoSession;
import com.meemaw.auth.sso.model.SsoUser;
import com.meemaw.shared.rest.response.Boom;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class CookieAuthDynamicFeature implements DynamicFeature {

  @Inject
  SsoDatasource ssoDatasource;

  @Inject
  InsightPrincipal principal;

  @Override
  public void configure(ResourceInfo resourceInfo, FeatureContext context) {
    CookieAuth annotation = resourceInfo.getResourceMethod().getAnnotation(CookieAuth.class);
    if (annotation == null) {
      annotation = resourceInfo.getResourceClass().getAnnotation(CookieAuth.class);
    }

    if (annotation != null) {
      context.register(new CookieAuthFilter());
    }
  }

  @Priority(Priorities.AUTHENTICATION)
  private class CookieAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
      Map<String, Cookie> cookies = requestContext.getCookies();
      Cookie ssoCookie = cookies.get(SsoSession.COOKIE_NAME);

      if (ssoCookie == null) {
        log.info("Missing SessionId");
        throw Boom.status(Status.UNAUTHORIZED).exception();
      }

      Optional<SsoUser> maybeUser = ssoDatasource.findSession(ssoCookie.getValue())
          .toCompletableFuture()
          .join();

      SsoUser ssoUser = maybeUser.orElseThrow(() -> Boom.status(Status.UNAUTHORIZED).exception());
      boolean isSecure = requestContext.getUriInfo().getAbsolutePath().toString()
          .startsWith("https");
      requestContext.setSecurityContext(new InsightSecurityContext(ssoUser, isSecure));
      principal.as(ssoUser);
    }
  }
}
