package io.github.nahuel92;

import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DateFieldBugTest implements TestPropertyProvider {
    @Container
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres");

    @Inject
    private MyEntityRepository myEntityRepository;

    @Inject
    private JdbcOperations jdbcOperations;

    @Override
    public Map<String, String> getProperties() {
        if (!container.isRunning()) {
            container.start();
        }
        return Map.of(
                "datasources.default.driver-class-name", "org.postgresql.Driver",
                "datasources.default.url", container.getJdbcUrl(),
                "datasources.default.username", container.getUsername(),
                "datasources.default.password", container.getPassword()
        );
    }

    @Test
    @DisplayName("LocalDate fields should be retrieved as they are persisted. This test shows that the returned LocalDate is one day behind from the original value")
    void failureOnMappingLocalDate() {
        // given
        final var entity = new MyEntity(
                null,
                LocalDate.of(2024, 1, 1)
        );

        // when
        final var savedId = myEntityRepository.save(entity).id();

        // then
        final var saved = myEntityRepository.findById(savedId).orElseThrow();
        assertThat(saved.createdOn()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    @DisplayName("LocalDate fields are mapped ok using JdbcOperations")
    void successOnMappingLocalDate() {
        // given
        final var localDate = LocalDate.of(2024, 1, 1);

        // when
        jdbcOperations.prepareStatement(
                "INSERT INTO my_entity(created_on) VALUES (?)",
                s -> {
                    s.setDate(1, java.sql.Date.valueOf(localDate));
                    s.executeUpdate();
                    return null;
                }
        );

        // then
        final var savedLocalDate = jdbcOperations.prepareStatement(
                "SELECT created_on FROM my_entity WHERE id = 1",
                s -> {
                    final var resultSet = s.executeQuery();
                    if (resultSet.next()) {
                        return resultSet.getDate("created_on");
                    }
                    return null;
                }
        );

        assertThat(savedLocalDate.toLocalDate())
                .isEqualTo(LocalDate.of(2024, 1, 1));
    }
}
