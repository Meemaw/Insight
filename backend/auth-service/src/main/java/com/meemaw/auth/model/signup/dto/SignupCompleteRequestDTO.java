package com.meemaw.auth.model.signup.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meemaw.shared.validators.Password;

public class SignupCompleteRequestDTO extends SignupVerifyRequestDTO {

  @JsonProperty("password")
  @Password
  private String password;

  public String getPassword() {
    return password;
  }
}
