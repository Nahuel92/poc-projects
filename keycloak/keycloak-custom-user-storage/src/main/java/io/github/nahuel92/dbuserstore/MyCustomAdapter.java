package io.github.nahuel92.dbuserstore;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import java.time.ZoneId;

public class MyCustomAdapter extends AbstractUserAdapterFederatedStorage {
    private final MyUserEntity entity;

    public MyCustomAdapter(final KeycloakSession keycloakSession, final RealmModel realm,
            final ComponentModel componentModel, final MyUserEntity entity) {
        super(keycloakSession, realm, componentModel);
        this.entity = entity;
    }

    @Override
    public String getUsername() {
        return entity.getUsername();
    }

    @Override
    public void setUsername(final String username) {
    }

    @Override
    public String getEmail() {
        return entity.getEmail();
    }

    @Override
    public String getLastName() {
        return entity.getLastName();
    }

    @Override
    public String getFirstName() {
        return entity.getFirstName();
    }

    @Override
    public Long getCreatedTimestamp() {
        return entity.getCreatedTimestamp()
                .atZone(ZoneId.of("America/New_York"))
                .toInstant()
                .toEpochMilli();
    }
}
