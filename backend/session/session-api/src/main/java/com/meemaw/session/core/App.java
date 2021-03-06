package com.meemaw.session.core;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title = "Session API",
        version = "1.0.0"
    ))
public class App extends Application {

}
