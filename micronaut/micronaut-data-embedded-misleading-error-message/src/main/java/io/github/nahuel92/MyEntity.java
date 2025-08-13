package io.github.nahuel92;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@MappedEntity("my_entity")
public record MyEntity(
        @MappedProperty("id") @Id @GeneratedValue @Nullable Long id,
        @Relation(Relation.Kind.EMBEDDED) @Valid @NotNull AuditFields auditFields) {
}
