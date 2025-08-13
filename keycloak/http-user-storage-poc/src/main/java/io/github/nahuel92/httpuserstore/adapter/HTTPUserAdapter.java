package io.github.nahuel92.httpuserstore.adapter;

import io.github.nahuel92.httpuserstore.dto.HTTPUserDTO;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.Objects;

public class HTTPUserAdapter extends AbstractUserAdapterFederatedStorage {
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String email;

    public HTTPUserAdapter(final KeycloakSession session, final RealmModel realm,
                           final ComponentModel storageProviderModel, final HTTPUserDTO httpUserDto) {
        super(session, realm, storageProviderModel);
        this.username = httpUserDto.username();
        this.password = httpUserDto.password();
        this.firstName = httpUserDto.firstName();
        this.lastName = httpUserDto.lastName();
        this.email = httpUserDto.email();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(final String username) {
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        HTTPUserAdapter that = (HTTPUserAdapter) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password)
                && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, password, email);
    }
}