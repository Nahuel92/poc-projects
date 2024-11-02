package io.github.nahuel92;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.nahuel92.wiremock.micronaut.ConfigureWireMock;
import io.github.nahuel92.wiremock.micronaut.EnableWireMock;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
@EnableWireMock(@ConfigureWireMock(name = "test-client", properties = "client.url"))
class MicronautCircuitBreakerPocTest {
    @Inject
    private RestAPI restAPI;

    @Test
    @DisplayName("Does it make sense to keep retrying a Bad Request when it is clear that there is a client-side error?")
    void failureOnCallingEndpointWithABadRequest() {
        //given
        WireMock.stubFor(WireMock.get("/get").willReturn(WireMock.badRequest()));

        // when
        final var exception = assertThrows(
                HttpClientResponseException.class,
                restAPI::issueRequest
        );

        // then
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        WireMock.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/get")));
    }
}