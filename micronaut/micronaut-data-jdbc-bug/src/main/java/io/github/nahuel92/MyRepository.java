package io.github.nahuel92;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.GenericRepository;
import java.util.Collection;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface MyRepository extends GenericRepository<MyEntity, Integer> {
    @Query("""
                SELECT a.id AS id,
                       a.attribute AS attribute,
                       CASE
                        WHEN a.another_attribute = 'c' THEN 'INSERT'
                        WHEN a.another_attribute = 'u' THEN 'UPDATE'
                        WHEN a.another_attribute = 'd' THEN 'DELETE'
                        ELSE 'UNKNOWN'
                       END AS operation
                FROM my_table AS a
            """)
    Collection<MyEntity> findAll();
}
