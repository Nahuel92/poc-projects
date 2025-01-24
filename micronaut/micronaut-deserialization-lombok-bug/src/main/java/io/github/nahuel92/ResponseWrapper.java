package io.github.nahuel92;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Introspected
@Serdeable.Deserializable
public record ResponseWrapper<@NotNull @Valid T>(
        @JsonProperty("status") String status,
        @JsonProperty("response") T response
) {
}
