package com.meemaw.auth.user.model;

import java.util.UUID;
import lombok.Value;

@Value
public class UserWithPasswordHashDTO {

  UUID id;
  String email;
  UserRole role;
  String org;
  String hashedPassword;

  public UserDTO user() {
    return new UserDTO(id, email, role, org);
  }
}
