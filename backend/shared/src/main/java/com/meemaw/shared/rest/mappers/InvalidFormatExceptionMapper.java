package com.meemaw.shared.rest.mappers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.meemaw.shared.rest.response.Boom;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {

  @Override
  public Response toResponse(InvalidFormatException exception) {
    return Boom.status(Status.BAD_REQUEST)
        .message(exception.getOriginalMessage())
        .response();
  }
}
