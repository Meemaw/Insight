package com.meemaw.auth.signup.model;

import static com.meemaw.test.matchers.SameJSON.sameJson;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meemaw.auth.signup.model.dto.SignupRequestDTO;
import com.meemaw.test.rest.mappers.JacksonMapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class SignupRequestTest {

  @Test
  public void dtoJacksonSerialization() throws JsonProcessingException {
    UUID token = UUID.fromString("bc2a1cc5-62ed-45a2-b7a6-70520dadc33b");
    UUID userId = UUID.fromString("cc2a1cc5-62ed-45a2-b7a6-70520dadc33b");

    OffsetDateTime createdAt = OffsetDateTime.of(
        LocalDateTime.of(2017, 05, 12, 05, 45),
        ZoneOffset.ofHoursMinutes(6, 30));

    SignupRequestDTO signupRequest = new SignupRequestDTO(
        "test@gmail.com",
        "ORG",
        token,
        userId,
        createdAt);

    String payload = JacksonMapper.get().writeValueAsString(signupRequest);
    assertThat(payload, sameJson(
        "{\"email\":\"test@gmail.com\",\"org\":\"ORG\",\"token\":\"bc2a1cc5-62ed-45a2-b7a6-70520dadc33b\",\"userId\":\"cc2a1cc5-62ed-45a2-b7a6-70520dadc33b\",\"createdAt\":{\"offset\":{\"totalSeconds\":23400,\"id\":\"+06:30\",\"rules\":{\"fixedOffset\":true,\"transitions\":[],\"transitionRules\":[]}},\"dayOfWeek\":\"FRIDAY\",\"dayOfYear\":132,\"month\":\"MAY\",\"nano\":0,\"year\":2017,\"monthValue\":5,\"dayOfMonth\":12,\"hour\":5,\"minute\":45,\"second\":0}}"));
  }
}
