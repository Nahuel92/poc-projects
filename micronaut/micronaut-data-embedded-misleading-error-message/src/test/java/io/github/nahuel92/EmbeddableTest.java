package io.github.nahuel92;

import io.micronaut.core.annotation.NonNull;
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
import static org.assertj.core.api.Assertions.assertThatCode;

@MicronautTest
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmbeddableTest implements TestPropertyProvider {
    @Container
    private final static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:11-alpine");

    @Inject
    private MyEntityRepository myEntityRepository;

    @Override
    @NonNull
    public Map<String, String> getProperties() {
        if (!container.isRunning()) {
            container.start();
        }
        return Map.of(
                "datasources.default.driver-class-name", "org.postgresql.Driver",
                "datasources.default.url", container.getJdbcUrl(),
                "datasources.default.username", container.getUsername(),
                "datasources.default.password", container.getPassword(),
                "datasources.default.timezone", "UTC"
        );
    }

    @Test
    @DisplayName("Current: When a mandatory field in an @Embeddable is null, the error says the embeddable is null")
    void failureOnFindingEntityDueToNullMandatoryFieldInEmbeddable() {
        // given
        final var currentErrorMessage = """
                Error instantiating entity [io.github.nahuel92.MyEntity]: Null argument specified for [auditFields]. \
                If this argument is allowed to be null annotate it with @Nullable""";

        // expect
        assertThatCode(() -> myEntityRepository.findById(1L)).hasMessage(currentErrorMessage);
    }

    @Test
    @DisplayName("Suggested: When a mandatory field in an @Embeddable is null, the error mentions which field is null")
    void failureOnFindingEntityDueToNullMandatoryFieldInEmbeddable2() {
        // given
        final var suggestedErrorMessage = """
                Error instantiating entity [io.github.nahuel92.MyEntity]: Null argument specified for \
                [auditFields.createdBy]. If this argument is allowed to be null annotate it with @Nullable""";

        // expect
        assertThatCode(() -> myEntityRepository.findById(1L)).hasMessage(suggestedErrorMessage);
    }
}
