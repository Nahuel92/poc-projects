package io.github.nahuel92;

import io.micronaut.data.annotation.Repository;
import java.util.List;

// Works at runtime (production mode, that is not when running tests)
@Repository
public abstract class BuggedRepository {
    @io.micronaut.transaction.annotation.Transactional
    public List<Integer> buggedFetchMicronaut() {
        return List.of(5, 10, 15);
    }

    @jakarta.transaction.Transactional
    public List<Integer> buggedFetchJakarta() {
        return List.of(5, 10, 15);
    }
}
