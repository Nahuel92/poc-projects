package io.github.nahuel92;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
@WireMockTest(httpPort = 65000)
class SerdeableDeserializableTest {
    @Inject
    private MyClient myClient;

    @Test
    void testItWorks() {
        // given
        WireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/get"))
                        .willReturn(WireMock.okJson("{ \"strs\": [ \"1\", \"2\", \"3\" ] }"))
        );

        // when
        final var res = myClient.get();

        // then
        Assertions.assertFalse(res.strs().isEmpty());
    }
}