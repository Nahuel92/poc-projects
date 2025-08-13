package io.github.nahuel92;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.serde.annotation.Serdeable;

@Controller("/")
class HelloController {
    @Get(value = "/greet", processes = MediaType.APPLICATION_JSON)
    Greet greet(@QueryValue("name") final String name) {
        return new Greet(name);
    }

    @Serdeable
    record Greet(String greet) {
    }
}