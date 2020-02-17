package com.meemaw.auth.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class UserDTO {

  @JsonProperty("id")
  protected UUID id;

  @JsonProperty("email")
  protected String email;

  @JsonProperty("role")
  protected UserRole role;

  @JsonProperty("org")
  protected String org;

  public UserDTO() {
  }

  public UserDTO(UUID id, String email, UserRole role, String org) {
    this.id = id;
    this.email = email;
    this.role = role;
    this.org = org;
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public UserRole getRole() {
    return role;
  }

  public String getOrg() {
    return org;
  }
}
