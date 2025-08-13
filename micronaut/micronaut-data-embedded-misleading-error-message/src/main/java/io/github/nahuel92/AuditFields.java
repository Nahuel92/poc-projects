package io.github.nahuel92;

import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.MappedProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Embeddable
public record AuditFields(
        @MappedProperty("created_on") @NotNull LocalDate createdOn,
        @MappedProperty("created_by") @NotBlank String createdBy) {
}
