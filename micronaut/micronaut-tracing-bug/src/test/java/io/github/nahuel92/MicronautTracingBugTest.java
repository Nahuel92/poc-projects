package io.github.nahuel92;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WireMockTest(httpPort = 9999)
@MicronautTest(rebuildContext = true)
class MicronautTracingBugTest {
    @Inject
    private MyClient myClient;

    @Test
    @Property(name = "micronaut.otel.enabled", value = "true")
    @DisplayName("When OTEL is enabled, it fails to find the kotlin.jvm.internal.Intrinsics class")
    void failureOnGettingMessage() {
        // given
        WireMock.stubFor(WireMock.get("/message").willReturn(WireMock.ok("Hello World")));

        // expect
        Assertions.assertThat(myClient.getMessage()).isEqualTo("Hello World");
    }

    @Test
    @Property(name = "micronaut.otel.enabled", value = "false")
    void successOnGettingMessage() {
        // given
        WireMock.stubFor(WireMock.get("/message").willReturn(WireMock.ok("Hello World")));

        // expect
        Assertions.assertThat(myClient.getMessage()).isEqualTo("Hello World");
    }
}
