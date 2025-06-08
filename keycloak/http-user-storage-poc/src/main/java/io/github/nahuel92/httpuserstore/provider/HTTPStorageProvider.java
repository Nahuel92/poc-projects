package io.github.nahuel92.httpuserstore.provider;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.nahuel92.httpuserstore.adapter.HTTPUserAdapter;
import io.github.nahuel92.httpuserstore.dto.HTTPUserDTO;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class HTTPStorageProvider implements UserLookupProvider, UserStorageProvider, UserQueryProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPStorageProvider.class);
    private final KeycloakSession keycloakSession;
    private final ComponentModel componentModel;

    public HTTPStorageProvider(final KeycloakSession keycloakSession, final ComponentModel componentModel) {
        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(final RealmModel realm, final String id) {
        final var storageId = new StorageId(id);
        final var username = storageId.getExternalId();
        return getUserByUsername(realm, username);
    }

    @Override
    public UserModel getUserByUsername(final RealmModel realmModel, final String username) {
        return fetchUserByUsername(realmModel, username);
    }

    @Override
    public UserModel getUserByEmail(final RealmModel realmModel, final String email) {
        return fetchUserByEmail(realmModel, email);
    }

    @Override
    public int getUsersCount(final RealmModel realm, final boolean includeServiceAccount) {
        return (int) fetchUsers(realm).count();
    }

    @Override
    public Stream<UserModel> searchForUserStream(final RealmModel realm, final Map<String, String> params,
                                                 final Integer firstResult, final Integer maxResults) {
        return fetchUsers(realm);
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(final RealmModel realm, final GroupModel group,
                                                   final Integer firstResult, final Integer maxResults) {
        LOGGER.debug("Method 'getGroupMembersStream' not implemented");
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(final RealmModel realm, final String attrName,
                                                                final String attrValue) {
        LOGGER.debug("Method 'searchForUserByUserAttributeStream' not implemented");
        return Stream.empty();
    }

    private UserModel fetchUserByUsername(final RealmModel realmModel, final String username) {
        return fetchUserByAttribute(realmModel, "username", username);
    }

    private UserModel fetchUserByEmail(final RealmModel realmModel, final String email) {
        return fetchUserByAttribute(realmModel, "email", email);
    }

    private UserModel fetchUserByAttribute(final RealmModel realmModel, final String attribute, final String value) {
        return Optional.ofNullable(
                        fetchFromAPI(
                                SimpleHttp.doGet(
                                        "http://host.docker.internal:3000/users?" + attribute + "=" + value,
                                        keycloakSession
                                ),
                                new TypeReference<HTTPUserDTO>() {
                                }
                        )
                )
                .map(e -> new HTTPUserAdapter(keycloakSession, realmModel, componentModel, e))
                .orElse(null);
    }

    private Stream<UserModel> fetchUsers(final RealmModel realmModel) {
        return fetchFromAPI(
                SimpleHttp.doGet(
                        "http://host.docker.internal:3000/users",
                        keycloakSession
                ),
                new TypeReference<Collection<HTTPUserDTO>>() {
                }
        )
                .stream()
                .map(e -> new HTTPUserAdapter(keycloakSession, realmModel, componentModel, e));
    }

    private <T> T fetchFromAPI(final SimpleHttp httpRequest, final TypeReference<T> typeReference) {
        try {
            return httpRequest.asJson(typeReference);
        } catch (final IOException e) {
            LOGGER.error("Failed to read json", e);
            throw new UncheckedIOException(e);
        }
    }
}
