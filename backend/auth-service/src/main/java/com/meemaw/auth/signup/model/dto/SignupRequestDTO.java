package com.meemaw.auth.signup.model.dto;

import com.meemaw.auth.shared.Expirable;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Value;

@Value
public class SignupRequestDTO implements Expirable {

  String email;
  String org;
  UUID token;
  UUID userId;
  OffsetDateTime createdAt;

}
