package io.github.nahuel92;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import jakarta.annotation.Nullable;

@MappedEntity("test")
public record MyEntity(
        @Id @GeneratedValue @Nullable Integer id,
        @MappedProperty("first_name") String name) {
}
