package io.github.nahuel92.dbuserstore;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class StorageProviderFactory implements UserStorageProviderFactory<StorageProvider> {
    @Override
    public StorageProvider create(final KeycloakSession keycloakSession, final ComponentModel componentModel) {
        return new StorageProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return "my-storage-provider";
    }

    @Override
    public String getHelpText() {
        return "This is a customer user storage provider that uses H2 as backend";
    }
}
