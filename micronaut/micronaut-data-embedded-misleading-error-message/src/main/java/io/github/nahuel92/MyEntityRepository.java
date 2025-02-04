package io.github.nahuel92;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MyEntityRepository extends CrudRepository<@Valid MyEntity, Long> {
}
