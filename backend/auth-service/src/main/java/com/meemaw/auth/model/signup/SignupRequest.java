package com.meemaw.auth.model.signup;

import com.meemaw.auth.model.signup.dto.SignupVerifyRequestDTO;
import java.util.Objects;
import java.util.UUID;

public class SignupRequest extends SignupVerifyRequestDTO {

  private final String email;
  private UUID userId;

  public SignupRequest(String email) {
    this.email = Objects.requireNonNull(email);
  }

  public SignupRequest org(String org) {
    this.org = Objects.requireNonNull(org);
    return this;
  }

  public SignupRequest token(UUID token) {
    this.token = Objects.requireNonNull(token);
    return this;
  }

  public SignupRequest userId(UUID userId) {
    this.userId = Objects.requireNonNull(userId);
    return this;
  }

  public UUID getUserId() {
    return userId;
  }

  @Override
  public String getEmail() {
    return email;
  }
}
