package io.github.nahuel92;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
class MyServiceTest {
    @Inject
    private EmbeddedApplication<?> application;

    @Inject
    private MyService subject;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }
}