package io.github.nahuel92;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@MappedEntity(value = "my_entity")
public record MyEntity(
        @MappedProperty("id") @Id @GeneratedValue @Nullable Integer id,
        @MappedProperty("field_1") @NotBlank String field1,
        @MappedProperty("field_2") @NotNull @Min(0) @Max(2) Integer field2) {
}
