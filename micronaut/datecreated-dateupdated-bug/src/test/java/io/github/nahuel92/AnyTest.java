package io.github.nahuel92;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnyTest {

    @Test
    void name() {
        final var a = new MyEntity();
        Assertions.assertThat(a.getFirstName()).isNull();
    }
}
