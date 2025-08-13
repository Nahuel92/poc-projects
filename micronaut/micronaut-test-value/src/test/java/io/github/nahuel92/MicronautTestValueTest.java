package io.github.nahuel92;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Value;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Property(name = "myProp", value = "any")
@MicronautTest
class MicronautTestValueTest {
    @Test
    void failureOnInjectingPlaceholderValue(@Value("${myProp}") final String myValue) {
        assertThat(myValue).isEqualTo("any");
    }

    @Test
    void successOnInjectingPropertyValue(@Value("myProp") final String myValue) {
        assertThat(myValue).isEqualTo("any");
    }

    @Test
    void successOnInjectingProperty(@Property(name = "myProp") final String myValue) {
        assertThat(myValue).isEqualTo("any");
    }
}
