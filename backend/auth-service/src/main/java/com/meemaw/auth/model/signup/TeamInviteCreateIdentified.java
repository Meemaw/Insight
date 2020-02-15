package com.meemaw.auth.model.signup;

import com.meemaw.auth.model.signup.dto.TeamInviteCreateDTO;
import com.meemaw.auth.model.user.UserRole;
import java.util.UUID;

public class TeamInviteCreateIdentified extends TeamInviteCreateDTO {

  protected UUID creator;
  protected String org;

  public TeamInviteCreateIdentified(UUID creator, String org, String email, UserRole role) {
    this.email = email;
    this.org = org;
    this.role = role;
    this.creator = creator;
  }

  public UUID getCreator() {
    return creator;
  }

  public String getOrg() {
    return org;
  }
}
