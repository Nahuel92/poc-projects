package io.github.nahuel92;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedProperty;

@Introspected
public record MyEntity(
        @MappedProperty("id") Integer id,
        @MappedProperty("attribute") String attribute,
        @MappedProperty("operation") String operation) {
}
