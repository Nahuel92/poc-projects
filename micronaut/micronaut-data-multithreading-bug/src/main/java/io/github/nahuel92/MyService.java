package io.github.nahuel92;

import jakarta.inject.Singleton;

import java.util.concurrent.Executors;

@Singleton
public class MyService {
    private final MyRepo myRepo;

    MyService(final MyRepo myRepo) {
        this.myRepo = myRepo;
    }

    public void doSomeWorkOnSameThread(final MyEntity myEntity) {
        myRepo.save(myEntity);
    }

    public void doSomeWorkInAnotherThread(final MyEntity myEntity) {
        try (final var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.execute(() -> myRepo.save(myEntity));
        }
    }
}