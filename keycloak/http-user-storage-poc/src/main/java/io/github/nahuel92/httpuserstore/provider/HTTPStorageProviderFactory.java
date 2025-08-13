package io.github.nahuel92.httpuserstore.provider;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class HTTPStorageProviderFactory implements UserStorageProviderFactory<HTTPStorageProvider> {
    @Override
    public HTTPStorageProvider create(final KeycloakSession keycloakSession, final ComponentModel componentModel) {
        return new HTTPStorageProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return "test-http-provider";
    }
}
