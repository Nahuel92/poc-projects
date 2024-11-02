package io.github.nahuel92.httpuserstore;

import jakarta.ws.rs.client.ClientBuilder;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Stream;

public class HttpStorageProvider implements UserStorageProvider, UserQueryProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpStorageProvider.class);

    public HttpStorageProvider() {
        LOGGER.info("Creating HTTP client...");
        try (var client = ClientBuilder.newClient();) {
            LOGGER.info("It will never enter here");
        } catch (Exception e) {
            LOGGER.error("Error: '{}'", e.getMessage());
            throw e;
        }
    }

    @Override
    public void close() {
        // nop
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.empty();
    }
}
