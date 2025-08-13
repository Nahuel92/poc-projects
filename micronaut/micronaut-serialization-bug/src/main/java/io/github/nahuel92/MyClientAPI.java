package io.github.nahuel92;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client(value = "http://localhost:65000/myapi")
public interface MyClientAPI {
    @Get(produces = MediaType.APPLICATION_JSON)
    MyDTO get();
}