package com.meemaw.auth.sso.model;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.RandomStringUtils;

public class SsoSession {

  private static final String COOKIE_PATH = "/";

  public static final String COOKIE_NAME = "SessionId";

  private static final int SECONDS_IN_DAY = 60 * 60 * 24;

  // Keep session for 365 days
  public static final int TTL = SECONDS_IN_DAY * 365;
  // Invalidate session after 14 days of inactivity
  public static final int MAX_IDLE = SECONDS_IN_DAY * 14;

  // Number of characters in cookie
  public static final int SIZE = 50;

  public static NewCookie cookie(String sessionId, String domain) {
    return newCookie(sessionId, domain, TTL);
  }

  public static NewCookie clearCookie(String domain) {
    return newCookie(null, domain, 0);
  }

  private static NewCookie newCookie(String sessionId, String domain, int maxAge) {
    return new NewCookie(COOKIE_NAME, sessionId, COOKIE_PATH, domain, null, maxAge, false);
  }

  public static String newIdentifier() {
    return RandomStringUtils.randomAlphanumeric(SIZE);
  }

  public static Response cookieResponse(String sessionId, String cookieDomain) {
    return Response.noContent().cookie(SsoSession.cookie(sessionId, cookieDomain)).build();
  }
}
