package io.github.nahuel92;

import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.test.annotation.Sql;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.awaitility.Awaitility.await;

@Sql(
        phase = Sql.Phase.BEFORE_ALL,
        scripts = "classpath:db/ddl.sql"
)
@MicronautTest
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MicronautMultiThreadingBugTest implements TestPropertyProvider {
    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres"));

    @Inject
    private MyService myService;

    @Inject
    private MyRepo myRepo;

    @Inject
    private DataSource dataSource;

    @Override
    public Map<String, String> getProperties() {
        if (!container.isRunning()) {
            container.start();
        }
        return Map.ofEntries(
                Map.entry("datasources.default.driver-class-name", "org.postgresql.Driver"),
                Map.entry("datasources.default.dialect", "POSTGRES"),
                Map.entry("datasources.default.url", container.getJdbcUrl()),
                Map.entry("datasources.default.username", container.getUsername()),
                Map.entry("datasources.default.password", container.getPassword())
        );
    }

    @Test
    @DisplayName("When Datasource is set as read only, write transactions should fail")
    void failureOnExecutingTransactionWhenDatasourceIsSetAsReadOnlyAndCodeRunsInSameThread() throws SQLException {
        // given
        assumeThat(dataSource.getConnection().isReadOnly())
                .as("Datasource is correctly set as read only by TxReadOnlyEnforcer")
                .isTrue();
        // and
        final var myEntity = new MyEntity(null, "Morty");

        // expect
        assertThatCode(() -> myService.doSomeWorkOnSameThread(myEntity))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("When Datasource is set as read only, write transactions should fail even if run on another thread")
    void failureOnExecutingTransactionWhenDatasourceIsSetAsReadOnlyAndCodeRunsInAnotherThread() throws SQLException {
        // given
        assumeThat(dataSource.getConnection().isReadOnly())
                .as("Datasource is correctly set as read only by TxReadOnlyEnforcer")
                .isTrue();
        // and
        final var myEntity = new MyEntity(null, "Morty");

        // expect
        await().atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThatCode(() -> myService.doSomeWorkInAnotherThread(myEntity))
                        .isInstanceOf(DataAccessException.class)
                );
    }

    @Test
    @DisplayName(
            "When Datasource is set as read only, write transactions run on another thread are successfully committed")
    void successOnPersistingDataEvenIfTheDatasourceIsSetAsReadOnly() throws SQLException {
        // given
        assumeThat(dataSource.getConnection().isReadOnly())
                .as("Datasource is correctly set as read only by TxReadOnlyEnforcer")
                .isTrue();
        // and
        final var myEntity = new MyEntity(null, "Morty");

        // expect
        await().atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                            myService.doSomeWorkInAnotherThread(myEntity);
                            assertThat(myRepo.findById(1)).isPresent();
                        }
                );
    }
}