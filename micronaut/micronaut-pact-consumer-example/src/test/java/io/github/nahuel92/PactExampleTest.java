package io.github.nahuel92;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.apache.hc.client5.http.fluent.Request;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@PactConsumerTest
class PactExampleTest {
    @Pact(provider = "HelloProvider", consumer = "test_consumer")
    public V4Pact createPact(final PactDslWithProvider builder) {
        return builder
                .given("A test name")
                .uponReceiving("A greet request")
                .path("/greet")
                .queryParameterFromProviderState("name", "${name}", "Nahuel")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body(
                        new PactDslJsonBody()
                                .valueFromProviderState("greet", "${name}", "Nahuel")
                )
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(providerName = "HelloProvider")
    void successOnConsumingGreetAPI(final MockServer mockServer) throws IOException {
        // when
        final var response = Request.get(mockServer.getUrl() + "/greet?name=Nahuel")
                .execute()
                .returnContent();

        // then
        assertThat(response.asString(StandardCharsets.UTF_8)).isEqualTo("{\"greet\":\"Nahuel\"}");
    }
}