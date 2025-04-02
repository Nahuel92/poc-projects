package io.github.nahuel92.httpuserstore;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class HttpStorageProvider implements UserLookupProvider, UserStorageProvider, UserQueryProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpStorageProvider.class);
    private static final String USERS_QUERY_SEARCH = "keycloak.session.realm.users.query.search";
    private final KeycloakSession keycloakSession;
    private final ComponentModel componentModel;
    private final SimpleHttp httpClient;

    public HttpStorageProvider(final KeycloakSession keycloakSession, final ComponentModel componentModel) {
        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
        this.httpClient = SimpleHttp.doGet(
                "http://host.docker.internal:3000/keycloak",
                keycloakSession
        );
    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        final var storageId = new StorageId(id);
        final var username = storageId.getExternalId();
        return getUserByUsername(realm, username);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        return fetchFromAPI(username).stream()
                .findFirst()
                .map(e -> new HttpUserModel(keycloakSession, realm, componentModel, e))
                .orElse(null);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return null;
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return fetchFromAPI().size();
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult,
                                                 Integer maxResults) {
        return retrieveFromAPI(params)
                .stream()
                .map(e -> new HttpUserModel(keycloakSession, realm, componentModel, e));
    }

    private Set<HttpUserEntity> retrieveFromAPI(final Map<String, String> params) {
        final var filter = params.get(USERS_QUERY_SEARCH);
        if (filter != null) {
            return fetchFromAPI(filter);
        }
        return fetchFromAPI();
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult,
                                                   Integer maxResults) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.empty();
    }

    private Set<HttpUserEntity> fetchFromAPI() {
        return fetchFromAPI(httpClient);
    }

    private Set<HttpUserEntity> fetchFromAPI(final String filter) {
        return fetchFromAPI(httpClient.param("q", filter));
    }

    private Set<HttpUserEntity> fetchFromAPI(final SimpleHttp httpRequest) {
        try {
            return httpRequest.asJson(new TypeReference<>() {
            });
        } catch (final IOException e) {
            LOGGER.error("Failed to read json", e);
            throw new UncheckedIOException(e);
        }
    }
}
