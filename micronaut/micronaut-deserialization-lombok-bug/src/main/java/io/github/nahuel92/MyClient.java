package io.github.nahuel92;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import jakarta.validation.Valid;

@Client(value = "http://localhost:65000")
public interface MyClient {
    @Get(value = "/get", produces = MediaType.APPLICATION_JSON)
    @Valid
    MyDTO get();
}