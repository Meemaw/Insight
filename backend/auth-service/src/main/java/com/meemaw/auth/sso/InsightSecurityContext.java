package com.meemaw.auth.sso;

import com.meemaw.auth.model.sso.SsoUser;
import com.meemaw.auth.model.user.UserRole;
import java.security.Principal;
import javax.ws.rs.core.SecurityContext;

public class InsightSecurityContext implements SecurityContext {

  private Principal principal;
  private boolean isSecure;
  private UserRole userRole;

  public InsightSecurityContext(SsoUser ssoUser, boolean isSecure) {
    this.userRole = ssoUser.getRole();
    this.isSecure = isSecure;
    this.principal = () -> ssoUser.getId().toString();
  }

  @Override
  public Principal getUserPrincipal() {
    return principal;
  }

  @Override
  public boolean isSecure() {
    return isSecure;
  }

  @Override
  public boolean isUserInRole(String role) {
    return UserRole.valueOf(role).equals(userRole);
  }

  @Override
  public String getAuthenticationScheme() {
    return "Cookie-Based-Auth-Scheme";
  }
}
