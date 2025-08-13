package io.github.nahuel92;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AppTest {
    @Test
    void successOnTestingSimpleSum() {
        // when
        final var result = App.sum(1, 2);

        // then
        Assertions.assertEquals(3, result);
    }
}
