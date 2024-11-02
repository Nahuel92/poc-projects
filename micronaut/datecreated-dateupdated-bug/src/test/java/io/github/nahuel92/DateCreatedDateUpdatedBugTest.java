package io.github.nahuel92;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DateCreatedDateUpdatedBugTest implements TestPropertyProvider {
    @Container
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres");

    @Inject
    private MyEntityRepository myEntityRepository;

    @Inject
    private MyEntity2Repository myEntity2Repository;

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
    @DisplayName("Micronaut doesn't fill in fields if they are members of an Embeddable class used by an entity")
    void successOnAutoFillingAuditFields() {
        // given
        final var entity = new MyEntity();
        entity.setFirstName("Morty");

        // when
        final var saved = myEntityRepository.save(entity);

        // then
        assertThat(saved.getAuditFields().getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Micronaut automatically fills in fields if they are directly members of the entity")
    void successOnAutoFillingAuditFields2() {
        // given
        final var entity2 = new MyEntity2();
        entity2.setFirstName("Morty");

        // when
        final var saved = myEntity2Repository.save(entity2);

        // then
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
