package com.meemaw.session.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.meemaw.test.rest.mappers.JacksonMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class PageIdentitySerializationTest {

  @Test
  public void browserEventDeserialization() throws JsonProcessingException {
    String payload =
        "{\"uid\":\"a42a7f2d-756a-4d69-ba84-e8e76ea6200d\",\"sessionId\":\"c060bf17-70b9-4f46-b9c4-49b682995187\",\"pageId\":\"33962a42-f401-4486-8a2a-6015a238a72b\"}";
    PageIdentity deserialized = JacksonMapper.get().readValue(payload, PageIdentity.class);
    assertEquals(PageIdentity.class, deserialized.getClass());

    assertEquals(UUID.fromString("a42a7f2d-756a-4d69-ba84-e8e76ea6200d"), deserialized.getUid());
  }
}
