package com.meemaw.auth.model.signup.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meemaw.auth.model.user.UserRole;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TeamInviteCreateDTO {

  @JsonProperty("email")
  @NotBlank(message = "Required")
  @Email
  protected String email;

  @JsonProperty("role")
  @NotNull(message = "Required")
  protected UserRole role;

  public String getEmail() {
    return email;
  }

  public UserRole getRole() {
    return role;
  }

}
