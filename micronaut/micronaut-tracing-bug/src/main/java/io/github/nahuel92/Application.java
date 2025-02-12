package io.github.nahuel92;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.Micronaut;
import jakarta.inject.Singleton;

public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}

@Singleton
record A(MyClient myClient) implements ApplicationEventListener<StartupEvent> {
    @Override
    public void onApplicationEvent(final StartupEvent ignored) {
        myClient.getMessage();
    }
}