package com.meemaw.auth.model.signup.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meemaw.auth.model.user.UserRole;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TeamInviteDTO extends TeamInviteCreateDTO {

  @JsonProperty("token")
  private UUID token;

  @JsonProperty("createdAt")
  private OffsetDateTime createdAt;

  public TeamInviteDTO(String email, UserRole role, UUID token, OffsetDateTime createdAt) {
    this.email = email;
    this.role = role;
    this.token = token;
    this.createdAt = createdAt;
  }

  public UUID getToken() {
    return token;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }
}
