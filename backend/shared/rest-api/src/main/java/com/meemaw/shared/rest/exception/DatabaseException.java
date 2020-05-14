package com.meemaw.shared.rest.exception;

import com.meemaw.shared.rest.response.Boom;

public class DatabaseException extends BoomException {

  public DatabaseException(Throwable throwable) {
    super(
        throwable,
        Boom.serverError()
            .message("Something went wrong while trying access database, please try again"));
  }
}
