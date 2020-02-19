package com.meemaw.auth.password.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PasswordResetRequest {

  UUID token;
  UUID userId;
  String email;
  String org;
  OffsetDateTime createdAt;

}
