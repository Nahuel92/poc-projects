package io.github.nahuel92.httpuserstore;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class HttpUserAdapter extends AbstractUserAdapterFederatedStorage {
    private final HttpUserEntity entity;
    private final String keycloakId;

    public HttpUserAdapter(final KeycloakSession keycloakSession, final RealmModel realm,
                           final ComponentModel componentModel, final HttpUserEntity entity) {
        super(keycloakSession, realm, componentModel);
        this.entity = entity;
        this.keycloakId = StorageId.keycloakId(componentModel, entity.getId());
    }

    @Override
    public String getUsername() {
        return entity.getUsername();
    }

    @Override
    public void setUsername(final String username) {
        entity.setUsername(username);
    }

    @Override
    public void setEmail(final String email) {
        entity.setEmail(email);
    }

    @Override
    public String getEmail() {
        return entity.getEmail();
    }

    @Override
    public void setLastName(final String lastName) {
        entity.setLastName(lastName);
    }

    @Override
    public String getLastName() {
        return entity.getLastName();
    }

    @Override
    public void setFirstName(final String firstName) {
        entity.setFirstName(firstName);
    }

    @Override
    public String getFirstName() {
        return entity.getFirstName();
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    public String getPassword() {
        return entity.getPassword();
    }

    public void setPassword(final String password) {
        entity.setPassword(password);
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        if (name.equals("phone")) {
            entity.setPhone(value);
        } else {
            super.setSingleAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        if (name.equals("phone")) {
            entity.setPhone(null);
        } else {
            super.removeAttribute(name);
        }
    }

    @Override
    public void setAttribute(final String name, final List<String> values) {
        if ("phone".equals(name)) {
            entity.setPhone(values.getFirst());
            return;
        }
        super.setAttribute(name, values);
    }

    @Override
    public String getFirstAttribute(final String name) {
        if ("phone".equals(name)) {
            return entity.getPhone();
        }
        return super.getFirstAttribute(name);
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        final var attrs = super.getAttributes();
        final var all = new MultivaluedHashMap<String, String>();
        all.putAll(attrs);
        all.add("phone", entity.getPhone());
        return all;
    }

    @Override
    public Stream<String> getAttributeStream(final String name) {
        if (name.equals("phone")) {
            return Stream.of(entity.getPhone());
        }
        return super.getAttributeStream(name);
    }

    @Override
    public Long getCreatedTimestamp() {
        return super.getCreatedTimestamp();
    }

    @Override
    public void setCreatedTimestamp(Long timestamp) {
        super.setCreatedTimestamp(timestamp);
        entity.setCreatedTimestamp(timestamp);
    }
}
