package com.meemaw.auth.user.model;

import com.meemaw.shared.auth.UserRole;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;


@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserDTO {

  UUID id;
  String email;
  UserRole role;
  String org;
}
