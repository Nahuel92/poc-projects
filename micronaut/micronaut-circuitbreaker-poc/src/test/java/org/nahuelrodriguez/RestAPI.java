package io.github.nahuel92;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.retry.annotation.CircuitBreaker;

@CircuitBreaker
@Client("${client.url}")
interface RestAPI {
    @Get("/get")
    String issueRequest();
}
