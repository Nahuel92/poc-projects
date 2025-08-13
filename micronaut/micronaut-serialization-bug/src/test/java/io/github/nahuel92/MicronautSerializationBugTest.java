package io.github.nahuel92;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@WireMockTest(httpPort = 65000)
class MicronautSerializationBugTest {
    @Inject
    private MyClientAPI subject;

    @Test
    void successOnGettingResponse() {
        // given
        WireMock.stubFor(WireMock.get(urlPathTemplate("/myapi"))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
                .willReturn(okJson("{ \"items\": [1, 2, 3] }"))
        );

        // when
        final var result = subject.get();

        // then
        assertThat(result).isEqualTo(new MyDTO(List.of(1, 2, 3)));
        WireMock.verify(getRequestedFor(urlPathTemplate("/myapi")));
    }
}
