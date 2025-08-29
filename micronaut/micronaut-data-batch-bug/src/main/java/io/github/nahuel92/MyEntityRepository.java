package io.github.nahuel92;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MyEntityRepository extends CrudRepository<@NotNull @Valid MyEntity, Integer> {
    @Query("""
            SELECT EXISTS (
                SELECT 1
                FROM my_entity AS e
                WHERE (e.field1, e.field2) IN :myEntity
            )"""
    )
    boolean exists(MyEntity myEntity);

    @Query("""
            SELECT e
            FROM my_entity AS e
            WHERE (e.field1, e.field2) IN :myEntity"""
    )
    List<MyEntity> exists(Iterable<MyEntity> myEntity);
}
