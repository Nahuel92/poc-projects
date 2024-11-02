package io.github.nahuel92.httpuserstore;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class HttpStorageProviderFactory implements UserStorageProviderFactory<HttpStorageProvider> {
    @Override
    public HttpStorageProvider create(KeycloakSession keycloakSession, ComponentModel componentModel) {
        return new HttpStorageProvider();
    }

    @Override
    public String getId() {
        return "test-http-provider";
    }
}
