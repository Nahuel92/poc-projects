package io.github.nahuel92;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;

@MappedEntity(value = "my_entity")
public record MyEntity(
        @MappedProperty("id") @Id @GeneratedValue @Nullable Integer id,
        @MappedProperty("field_1") String field1,
        @MappedProperty("field_2") Integer field2) {
}
