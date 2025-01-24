package io.github.nahuel92;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@MicronautTest
@WireMockTest(httpPort = 65000)
class RecordLombokBuilderTest {
    @Inject
    private MyClient myClient;

    @Test
    @DisplayName("On a previous version, this test passes as expected. In newer versions it fails")
    void failureOnReadingANestedValidatedRecord() {
        // given
        WireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/get"))
                        .willReturn(WireMock.okJson("{}"))
        );

        // expect
        Assertions.assertThatThrownBy(() -> myClient.get())
                .hasMessageContaining("id: must not be null")
                .hasMessageContaining("commonDTO.commonField: must not be blank")
                .hasMessageContaining("commonDTO.commonField2: must not be null");
    }

    @Test
    @DisplayName("On a previous version, this test passes as expected. In newer versions it fails")
    void successOnReadingANestedValidatedRecord() {
        // given
        WireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/get"))
                        .willReturn(WireMock.okJson("{ \"id\": 1, \"common_field\": \"any\", \"common_field2\": \"any2\" }"))
        );

        // expect
        Assertions.assertThatThrownBy(() -> myClient.get())
                .doesNotThrowAnyException();
    }
}