package io.github.nahuel92;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client(value = "http://localhost:65000")
public interface MyClient {
    @Get("/get")
    MyDTO get();
}