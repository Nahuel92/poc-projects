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
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.OnUserCache;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class StorageProvider implements UserStorageProvider, UserLookupProvider, UserRegistrationProvider,
        UserQueryProvider, CredentialInputUpdater, CredentialInputValidator, OnUserCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageProvider.class);
    private static final String PASSWORD_CACHE_KEY = UserAdapter.class.getName() + ".password";
    private final KeycloakSession keycloakSession;
    private final ComponentModel componentModel;
    private final EntityManager entityManager;

    public StorageProvider(final KeycloakSession keycloakSession, final ComponentModel componentModel) {
        this.keycloakSession = keycloakSession;
        this.componentModel = componentModel;
        this.entityManager = keycloakSession.getProvider(JpaConnectionProvider.class, "my-user-store").getEntityManager();
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
        return supportsCredentialType(credentialType) && getPassword(user) != null;
    }

    @Override
    public boolean isValid(final RealmModel realm, final UserModel user, final CredentialInput credentialInput) {
        if (!supportsCredentialType(credentialInput.getType()) || !(credentialInput instanceof UserCredentialModel cred)) {
            return false;
        }
        final var password = getPassword(user);
        return password != null && password.equals(cred.getValue());
    }

    @Override
    public boolean updateCredential(final RealmModel realm, final UserModel user, final CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel cred)) {
            return false;
        }
        final var adapter = getUserAdapter(user);
        adapter.setPassword(cred.getValue());
        return true;
    }

    @Override
    public void disableCredentialType(final RealmModel realm, final UserModel user, final String credentialType) {
        if (!supportsCredentialType(credentialType)) {
            return;
        }
        getUserAdapter(user).setPassword(null);
    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(final RealmModel realm, final UserModel user) {
        if (getUserAdapter(user).getPassword() != null) {
            return Stream.of(PasswordCredentialModel.TYPE);
        }
        return Stream.empty();
    }

    @Override
    public UserModel getUserById(final RealmModel realm, final String id) {
        LOGGER.info("Retrieving user by id: '{}'", id);
        final var persistenceId = StorageId.externalId(id);
        final var entity = entityManager.find(UserEntity.class, persistenceId);
        if (entity == null) {
            LOGGER.info("Could not find user by id: '{}'", id);
            return null;
        }
        return new UserAdapter(keycloakSession, realm, componentModel, entity);
    }

    @Override
    public UserModel getUserByUsername(final RealmModel realm, final String username) {
        LOGGER.info("Retrieving user by username: '{}'", username);
        final var query = entityManager.createNamedQuery("getUserByUsername", UserEntity.class);
        query.setParameter("username", username);
        final var result = query.getResultList();
        if (result.isEmpty()) {
            LOGGER.info("Could not find username: '{}'", username);
            return null;
        }
        return new UserAdapter(keycloakSession, realm, componentModel, result.getFirst());
    }

    @Override
    public UserModel getUserByEmail(final RealmModel realm, final String email) {
        LOGGER.info("Retrieving user by email: '{}'", email);
        final var query = entityManager.createNamedQuery("getUserByEmail", UserEntity.class);
        query.setParameter("email", email);
        final var result = query.getResultList();
        if (result.isEmpty()) {
            LOGGER.info("Could not find email: '{}'", email);
            return null;
        }
        return new UserAdapter(keycloakSession, realm, componentModel, result.getFirst());
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
        return query.getResultStream().map(entity -> new UserAdapter(keycloakSession, realm, componentModel, entity));
    }

    private TypedQuery<UserEntity> getUserEntityTypedQuery(final Map<String, String> params) {
        final var search = StringUtils.defaultIfBlank(params.get(UserModel.SEARCH), StringUtils.EMPTY);
        if ("*".equals(search)) {
            return entityManager.createNamedQuery("getAllUsers", UserEntity.class);
        }
        return entityManager.createNamedQuery("searchForUser", UserEntity.class)
                .setParameter("search", "%" + search.toLowerCase() + "%");
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
        LOGGER.info("Adding user with username: '{}'", username);
        final var userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setUsername(username);
        userEntity.setCreatedTimestamp(ZonedDateTime.now().toEpochSecond());
        entityManager.persist(userEntity);
        return new UserAdapter(keycloakSession, realm, componentModel, userEntity);
    }

    @Override
    public boolean removeUser(final RealmModel realm, final UserModel user) {
        LOGGER.info("Removing user with username: '{}'", user.getUsername());
        final var persistenceId = StorageId.externalId(user.getId());
        final var entity = entityManager.find(UserEntity.class, persistenceId);
        if (entity == null) {
            return false;
        }
        entityManager.remove(entity);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCache(final RealmModel realmModel, final CachedUserModel cachedUserModel, final UserModel userModel) {
        LOGGER.info("Getting cached user model for user '{}'", userModel.getId());
        final var password = ((UserAdapter) userModel).getPassword();
        if (password != null) {
            cachedUserModel.getCachedWith().put(PASSWORD_CACHE_KEY, password);
        }
    }

    private String getPassword(final UserModel user) {
        if (user instanceof CachedUserModel u) {
            return (String) u.getCachedWith().get(PASSWORD_CACHE_KEY);
        }
        if (user instanceof UserAdapter u) {
            return u.getPassword();
        }
        return null;
    }

    private UserAdapter getUserAdapter(final UserModel user) {
        if (user instanceof CachedUserModel c) {
            return (UserAdapter) c.getDelegateForUpdate();
        }
        return (UserAdapter) user;
    }
}
