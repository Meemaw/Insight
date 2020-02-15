package com.meemaw.auth.model.signup;

import com.meemaw.auth.model.signup.dto.SignupVerifyRequestDTO;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Signup extends SignupVerifyRequestDTO {

  private UUID userId;
  private OffsetDateTime createdAt;

  public Signup(String email, String org, UUID token, UUID userId, OffsetDateTime createdAt) {
    this.email = email;
    this.org = org;
    this.token = token;
    this.createdAt = createdAt;
    this.userId = userId;
  }


  public UUID getUserId() {
    return userId;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }
}
