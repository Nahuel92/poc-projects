package io.github.nahuel92;

import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.test.annotation.Sql;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@MicronautTest
@Sql(
        phase = Sql.Phase.BEFORE_ALL,
        scripts = {
                "classpath:db/ddl.sql",
                "classpath:db/data.sql"
        }
)
class MicronautDataUpdateBugTest {
    @Inject
    private EntityRepository entityRepository;

    @Inject
    private JdbcOperations jdbcOperations;

    @Test
    @DisplayName("As per #CrudRepository.update JavaDocs, update should fail if entity has no ID assigned")
    void failureOnUpdatingEntityWithoutId() {
        // given
        final var savedEntity = entityRepository.findById(1L);
        Assumptions.assumeTrue(savedEntity.isPresent());
        // and
        final var updatedEntity = savedEntity.get();
        updatedEntity.setId(null);
        updatedEntity.setFirstName("Morty");

        // expect
        Assertions.assertThrows(
                RuntimeException.class, // Not sure if DataAccessException is thrown, so I'm catching a broader exception.
                () -> entityRepository.update(updatedEntity)
        );
    }

    @Test
    @DisplayName("As per #CrudRepository.update JavaDocs, update should fail if entity has no ID assigned")
    void failureOnUpdatingEntityWithoutIdAlternativeCheck() {
        // given
        final var savedEntity = entityRepository.findById(1L);
        Assumptions.assumeTrue(savedEntity.isPresent());
        // and
        final var updatedEntity = savedEntity.get();
        updatedEntity.setId(null);
        updatedEntity.setFirstName("Morty");

        // when
        entityRepository.update(updatedEntity); // This should throw an exception, but it doesn't.

        // then
        final var firstNames = getFirstNames();
        Assertions.assertFalse(firstNames.contains("Morty"));
        Assertions.assertTrue(firstNames.contains("Marty"));
    }

    private List<String> getFirstNames() {
        return jdbcOperations.prepareStatement(
                "SELECT first_name AS fn FROM my_entity",
                statement -> {
                    final var firstNames = new ArrayList<String>();
                    final var resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        firstNames.add(resultSet.getString("fn"));
                    }
                    return firstNames;
                }
        );
    }
}
