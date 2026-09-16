package io.github.nahuel92;

import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

@Singleton
public class MyService {
    private final Set<MyInterface> interfaces;

    public MyService(@NotEmpty final Set<MyInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public Set<MyInterface> interfaces() {
        return Set.copyOf(interfaces);
    }
}
