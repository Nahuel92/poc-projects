package io.github.nahuel92;

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
import java.util.Map;
import java.util.stream.Collectors;

@MicronautTest
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MicronautDataJdbcBugTest implements TestPropertyProvider {
    @Container
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres");

    @Inject
    private MyRepository myRepository;

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
    @DisplayName("The test fails because it seems that Micronaut Data is interpreting the [ CREATE | UPDATE | DELETE ]"
                 + " as if they were the reserved words from SQL instead of literal values")
    void successOnGettingResults() {
        // when
        final var results = myRepository.findAll();
        final var groupedResults = results.stream()
                .collect(Collectors.groupingBy(MyEntity::operation));

        // then
        Assertions.assertNotNull(groupedResults.get("CREATE"));
        Assertions.assertNotNull(groupedResults.get("UPDATE"));
        Assertions.assertNotNull(groupedResults.get("DELETE"));
        // and
        Assertions.assertEquals(1, groupedResults.get("CREATE").size());
        Assertions.assertEquals(1, groupedResults.get("UPDATE").size());
        Assertions.assertEquals(1, groupedResults.get("DELETE").size());
    }

}
