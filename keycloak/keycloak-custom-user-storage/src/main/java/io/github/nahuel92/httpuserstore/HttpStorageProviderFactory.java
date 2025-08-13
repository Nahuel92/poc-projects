package io.github.nahuel92.httpuserstore;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class HttpStorageProviderFactory implements UserStorageProviderFactory<HttpStorageProvider> {
    @Override
    public HttpStorageProvider create(final KeycloakSession keycloakSession, final ComponentModel componentModel) {
        return new HttpStorageProvider(keycloakSession, componentModel);
    }

    @Override
    public String getId() {
        return "http-storage-provider";
    }

    @Override
    public String getHelpText() {
        return "This is a custom user storage provider that uses an HTTP service as backend";
    }
}
