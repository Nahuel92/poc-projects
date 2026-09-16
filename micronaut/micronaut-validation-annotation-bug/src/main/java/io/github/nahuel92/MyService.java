package io.github.nahuel92;

import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

@Singleton
public class MyService {
    public MyService(@NotEmpty final Set<MyInterface> interfaces) {
    }
}
