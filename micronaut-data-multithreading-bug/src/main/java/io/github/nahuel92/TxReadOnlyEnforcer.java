package io.github.nahuel92;

import io.micronaut.configuration.jdbc.hikari.DatasourceConfiguration;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import jakarta.inject.Singleton;

@Singleton
record TxReadOnlyEnforcer() implements BeanCreatedEventListener<DatasourceConfiguration> {
    @Override
    public DatasourceConfiguration onCreated(final BeanCreatedEvent<DatasourceConfiguration> event) {
        final var configuration = event.getBean();
        configuration.setReadOnly(true);
        return configuration;
    }
}
