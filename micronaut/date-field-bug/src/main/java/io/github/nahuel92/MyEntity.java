package io.github.nahuel92;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;

@Serdeable
@MappedEntity(value = "my_entity")
public record MyEntity(
        @Id @GeneratedValue Integer id,
        @MappedProperty("created_on") LocalDate createdOn) {
}
