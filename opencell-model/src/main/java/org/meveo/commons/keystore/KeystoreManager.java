package org.meveo.commons.keystore;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.credential.PasswordCredential;
import org.wildfly.security.credential.store.CredentialStore;
import org.wildfly.security.credential.store.CredentialStoreException;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.interfaces.ClearPassword;

public class KeystoreManager {

    private static final Logger log = LoggerFactory.getLogger(KeystoreManager.class);

    private static CredentialStore credentialStore;

    private static final ServiceName SERVICE_NAME_CRED_STORE = ServiceName.of("org", "wildfly", "security", "credential-store");

    private static final String KEYSTORE_NAME = "opencellcredstore";

    public static boolean existKeystore() {
        return CurrentServiceContainer.getServiceContainer().getService(ServiceName.of(SERVICE_NAME_CRED_STORE, KEYSTORE_NAME)) != null;
    }

    public static boolean existCredential(String credentialAlias) {
        try {
            ServiceContainer registry = CurrentServiceContainer.getServiceContainer();
            assert registry != null;
            ServiceController<?> credStoreService = registry.getService(ServiceName.of(SERVICE_NAME_CRED_STORE, KEYSTORE_NAME));
            assert credStoreService != null;
            credentialStore = (CredentialStore) credStoreService.getValue();

            return credentialStore.exists(credentialAlias, PasswordCredential.class);
        } catch (CredentialStoreException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void addCredential(String credentialAlias, String password) {
        Password clearPassword = ClearPassword.createRaw(ClearPassword.ALGORITHM_CLEAR, password.toCharArray());

        try {
            ServiceContainer registry = CurrentServiceContainer.getServiceContainer();
            assert registry != null;
            ServiceController<?> credStoreService = registry.getService(ServiceName.of(SERVICE_NAME_CRED_STORE, KEYSTORE_NAME));
            assert credStoreService != null;
            credentialStore = (CredentialStore) credStoreService.getValue();

            credentialStore.store(credentialAlias, new PasswordCredential(clearPassword));
            credentialStore.flush();
        } catch (CredentialStoreException e) {
            e.printStackTrace();
        }
    }

    public static String retrieveCredential(String credentialAlias) {
        Password password;
        try {
            ServiceContainer registry = CurrentServiceContainer.getServiceContainer();
            assert registry != null;
            ServiceController<?> credStoreService = registry.getService(ServiceName.of(SERVICE_NAME_CRED_STORE, KEYSTORE_NAME));
            assert credStoreService != null;
            credentialStore = (CredentialStore) credStoreService.getValue();

            PasswordCredential passwordCred = credentialStore.retrieve(credentialAlias, PasswordCredential.class);

            if (passwordCred != null) {
                password = passwordCred.getPassword();

                if (password instanceof ClearPassword)
                {
                    return new String(((ClearPassword) password).getPassword());
                }
            }
            else {
                log.error("Keystore does not contain a password with credential alias {} to retrieve", credentialAlias);
            }
        } catch (CredentialStoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void removeCredential(String credentialAlias) {
        ServiceContainer registry = CurrentServiceContainer.getServiceContainer();
        assert registry != null;
        ServiceController<?> credStoreService = registry.getService(ServiceName.of(SERVICE_NAME_CRED_STORE, KEYSTORE_NAME));
        assert credStoreService != null;
        credentialStore = (CredentialStore) credStoreService.getValue();

        try {
            credentialStore.remove(credentialAlias, PasswordCredential.class);
            credentialStore.flush();
        } catch (CredentialStoreException e) {
            log.error("Keystore does not contain a password with credential alias {} to remove", credentialAlias);
        }
    }

}
