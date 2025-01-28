package io.github.nahuel92;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;

@Client("http://localhost:9999") // any host, hardcoded to keep things simple
public interface MyClient {
    @Get("/message")
    String getMessage();
}
