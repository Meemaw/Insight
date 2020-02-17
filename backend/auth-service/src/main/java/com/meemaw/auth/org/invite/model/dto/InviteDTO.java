package com.meemaw.auth.org.invite.model;

import com.meemaw.auth.user.model.UserRole;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class InviteDTO {

  String email;
  String org;
  UserRole role;
  UUID creator;
  UUID token;
  OffsetDateTime createdAt;
  
}
