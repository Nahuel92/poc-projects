package io.github.nahuel92;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.MappedProperty;
import java.time.LocalDateTime;

@Introspected
@Embeddable
public class AuditFields {
    @MappedProperty("createdAt")
    @DateCreated
    private LocalDateTime createdAt;

    @MappedProperty("updatedAt")
    @DateUpdated
    private LocalDateTime updatedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
