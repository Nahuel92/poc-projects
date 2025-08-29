package io.github.nahuel92;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.util.Map;
import java.util.Set;

@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MicronautDataBatchBugTest implements TestPropertyProvider {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:12.19-alpine3.19")
    );

    @Inject
    private MyEntityRepository subject;

    @Override
    @NonNull
    public Map<String, String> getProperties() {
        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
        }
        return Map.ofEntries(
                Map.entry("datasources.default.driver-class-name", "org.postgresql.Driver"),
                Map.entry("datasources.default.url", POSTGRES.getJdbcUrl()),
                Map.entry("datasources.default.username", POSTGRES.getUsername()),
                Map.entry("datasources.default.password", POSTGRES.getPassword())
        );
    }

    @Test
    @DisplayName("Single exists method: This test fails when I expect it to pass")
    void successOnExecutingExistMethod() {
        // precondition
        Assertions.assertFalse(subject.findAll().isEmpty(), () -> "The table `my_entity` is empty!");

        // given
        final var myEntity = new MyEntity(null, "test", 1);

        // when
        subject.exists(myEntity);
        final var result = Assertions.assertDoesNotThrow(() -> subject.exists(myEntity));

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Batch exist method: This test fails when I expect it to pass")
    void successOnExecutingBatchExistMethod() {
        // precondition
        Assertions.assertFalse(subject.findAll().isEmpty(), () -> "The table `my_entity` is empty!");

        // given
        final var myEntity = new MyEntity(null, "test", 1);

        // when
        subject.exists(Set.of(myEntity));
        final var result = Assertions.assertDoesNotThrow(() -> subject.exists(Set.of(myEntity)));

        // then
        Assertions.assertFalse(result.isEmpty());
        final var myEntityFromDB = result.getFirst();
        Assertions.assertEquals("test", myEntityFromDB.field1());
        Assertions.assertEquals(1, myEntityFromDB.field2());
    }
}
