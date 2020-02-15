package com.meemaw.auth.model.user;

import java.util.UUID;

public class UserWithPasswordHashDTO extends UserDTO {

  public UserWithPasswordHashDTO(UUID id, String email, UserRole role, String org,
      String password) {
    super(id, email, role, org);
    this.password = password;
  }

  private String password;

  public String getPassword() {
    return password;
  }

  public UserDTO user() {
    return new UserDTO(this.getId(), this.getEmail(), this.getRole(), this.getOrg());
  }
}
