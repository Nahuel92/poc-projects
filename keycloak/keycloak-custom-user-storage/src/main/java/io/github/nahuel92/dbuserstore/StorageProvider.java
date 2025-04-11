package io.github.nahuel92.dbuserstore;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.stream.Stream;

public class StorageProvider implements CredentialInputValidator, CredentialInputUpdater,
        UserLookupProvider, UserQueryProvider, UserRegistrationProvider, UserStorageProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageProvider.class);
    private final KeycloakSession keycloakSession;
    private final ComponentModel componentModel;
    private final EntityManager entityManager;

    public StorageProvider(final KeycloakSession keycloakSession, final ComponentModel componentModel) {
        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
        this.entityManager = keycloakSession.getProvider(JpaConnectionProvider.class, "my-user-store")
                .getEntityManager();
    }

    @Override
    public void close() {
        LOGGER.debug("Closing Storage Provider...");
    }

    @Override
    public boolean supportsCredentialType(final String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(final RealmModel realm, final UserModel user, final String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(final RealmModel realm, final UserModel user, final CredentialInput credentialInput) {
        if (!supportsCredentialType(credentialInput.getType())
            || !(credentialInput instanceof UserCredentialModel userCredentialModel)) {
            return false;
        }
        final var userFromDB = entityManager.find(MyUserEntity.class, user.getId());
        if (userFromDB == null || userFromDB.getPassword() == null) {
            return false;
        }
        // Important: This code is not production-ready.
        return userFromDB.getPassword().equals(userCredentialModel.getValue());
    }

    @Override
    public boolean updateCredential(final RealmModel realmModel, final UserModel userModel,
            final CredentialInput credentialInput) {
        if (PasswordCredentialModel.TYPE.equals(credentialInput.getType())) {
            throw new ReadOnlyException("user is read only for this update");
        }
        return false;
    }

    @Override
    public void disableCredentialType(final RealmModel realm, final UserModel user, final String credentialType) {
    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(final RealmModel realm, final UserModel user) {
        return Stream.empty();
    }

    @Override
    public UserModel getUserById(final RealmModel realm, final String id) {
        LOGGER.info("Retrieving user by id: '{}'", id);
        final var persistenceId = StorageId.externalId(id);
        return getUserByUsername(realm, persistenceId); // entityManager.find(MyUserEntity.class, persistenceId);
    }

    @Override
    public UserModel getUserByUsername(final RealmModel realm, final String username) {
        LOGGER.info("Retrieving user by username: '{}'", username);
        final var query = entityManager.createNamedQuery("getUserByUsername", MyUserEntity.class);
        query.setParameter("username", username);
        final var result = query.getResultList();
        if (result.isEmpty()) {
            LOGGER.info("Could not find username: '{}'", username);
            return null;
        }
        return new MyCustomAdapter(keycloakSession, realm, componentModel, result.getFirst());
    }

    @Override
    public UserModel getUserByEmail(final RealmModel realm, final String email) {
        LOGGER.info("Retrieving user by email: '{}'", email);
        final var query = entityManager.createNamedQuery("getUserByEmail", MyUserEntity.class);
        query.setParameter("email", email);
        final var result = query.getResultList();
        if (result.isEmpty()) {
            LOGGER.info("Could not find email: '{}'", email);
            return null;
        }
        return new MyCustomAdapter(keycloakSession, realm, componentModel, result.getFirst());
    }

    @Override
    public Stream<UserModel> searchForUserStream(final RealmModel realm, final Map<String, String> params,
            final Integer firstResult, final Integer maxResults) {
        LOGGER.info("Retrieving user stream: '{}'", params);
        final var query = getUserEntityTypedQuery(params);
        if (firstResult != null) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }
        return query.getResultStream()
                .map(entity -> new MyCustomAdapter(keycloakSession, realm, componentModel, entity));
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(final RealmModel realm, final GroupModel group,
            final Integer firstResult, final Integer maxResults) {
        LOGGER.info("Not implemented - Retrieving group members: '{}'", group);
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(final RealmModel realm, final String attrName,
            final String attrValue) {
        LOGGER.info("Not implemented - Retrieving user by user attribute: '{}'", attrValue);
        return Stream.empty();
    }

    @Override
    public UserModel addUser(final RealmModel realm, final String username) {
        return null;
    }

    @Override
    public boolean removeUser(final RealmModel realm, final UserModel user) {
        return false;
    }

    private TypedQuery<MyUserEntity> getUserEntityTypedQuery(final Map<String, String> params) {
        final var search = StringUtils.defaultIfBlank(params.get(UserModel.SEARCH), StringUtils.EMPTY);
        if ("*".equals(search)) {
            return entityManager.createNamedQuery("getAllUsers", MyUserEntity.class);
        }
        return entityManager.createNamedQuery("searchForUser", MyUserEntity.class)
                .setParameter("search", "%" + search.toLowerCase() + "%");
    }
}
