package com.meemaw.auth.signup.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Value;

@Value
public class SignupRequestDTO {

  String email;
  String org;
  UUID token;
  UUID userId;
  OffsetDateTime createdAt;

}
