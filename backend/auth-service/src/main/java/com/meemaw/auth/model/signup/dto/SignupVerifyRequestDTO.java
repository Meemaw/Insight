package com.meemaw.auth.model.signup.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SignupVerifyRequestDTO {

  @JsonProperty("email")
  @NotBlank(message = "Required")
  @Email
  protected String email;

  @JsonProperty("org")
  @NotBlank(message = "Required")
  protected String org;

  @JsonProperty("token")
  @NotNull(message = "Required")
  protected UUID token;

  public String getOrg() {
    return org;
  }

  public UUID getToken() {
    return token;
  }

  public String getEmail() {
    return email;
  }
}
