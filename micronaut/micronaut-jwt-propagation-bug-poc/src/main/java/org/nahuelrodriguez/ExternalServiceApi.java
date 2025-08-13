package io.github.nahuel92;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;

@Client("http://localhost:3000")
public interface ExternalServiceApi {
    // As of now, JWT token is not automatically propagated.
    @Get("/endpoint")
    String jwtShouldBeAutomaticallyPropagatedForThisCall();

    // Manual approach works well.
    @Get("/endpoint")
    String jwtIsManuallyPropagatedForThisCall(@Header(HttpHeaders.AUTHORIZATION) final String jwt);
}
