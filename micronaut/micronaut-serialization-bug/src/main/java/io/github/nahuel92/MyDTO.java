package io.github.nahuel92;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Introspected
@Serdeable.Deserializable
public record MyDTO(@JsonProperty("items") @Serdeable.Deserializable(as = ImmutableList.class) List<Integer> items) {
}
