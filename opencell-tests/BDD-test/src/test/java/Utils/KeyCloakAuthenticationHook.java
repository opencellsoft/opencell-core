package Utils;

import io.cucumber.core.exception.CucumberException;
import io.restassured.RestAssured;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import org.apache.http.impl.client.HttpClients;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class KeyCloakAuthenticationHook {
    private static final EnvironmentVariables environmentVariables = new SystemEnvironmentVariables();
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private static Map<String, String> tokens = new HashMap<>();
    private static String token;

    // static variable single_instance of type KeyCloakAuthenticationHook
    private static KeyCloakAuthenticationHook single_instance = null;

    // static method to create a single instance of KeyCloakAuthenticationHook class
    public static KeyCloakAuthenticationHook getInstance()
    {
        if (single_instance == null)
            single_instance = new KeyCloakAuthenticationHook();

        return single_instance;
    }

    private static void authenticateAsAdmin() {
        String adminUser = System.getProperty( Constants.USERNAME_OC_ADMIN );
        String adminPassword = System.getProperty( Constants.PASSWORD_OC_ADMIN );
        setToken(adminUser, adminPassword);
    }

    private static void setToken(String login, String password) {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        if (tokens.get( environmentVariables.getProperty(Constants.URL_OC) + login ) != null) {
            token = tokens.get( environmentVariables.getProperty(Constants.URL_OC) + login );
            return;
        }
        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", System.getProperty( Constants.KCL_SECRET ) );
        Configuration config = new Configuration( environmentVariables.getProperty( Constants.KCL_URL ),
                System.getProperty( Constants.KCL_REALM ), System.getProperty( Constants.KCL_CLIENT_ID ),
                clientCredentials, HttpClients.createDefault());
        AuthzClient authzClient = AuthzClient.create(config);
        AccessTokenResponse response = authzClient.obtainAccessToken(login, password);

        // generate a new token for each environment, for example for two env 'baq' and 'tnn'
        // we need two tokens
        if (response.getToken() != null) {
            token = response.getToken();
            tokens.put(environmentVariables.getProperty(Constants.URL_OC) + login, token);
        } else {
            throw new CucumberException("Could not acquire the KC token, please check if the KC is running");
        }
    }

//    public void setProperties() {
//        System.setProperty("adminUsername", "opencell.admin");
//        System.setProperty("adminPassword", "opencell.admin");
//        System.setProperty("superUsername", "opencell.superadmin");
//        System.setProperty("superPassword", "opencell.superadmin");
//        System.setProperty("opencell.keycloak.secret", "afe07e5a-68cb-4fb0-8b75-5b6053b07dc3");
//        System.setProperty("opencell.keycloak.url", "http://localhost:8080/auth");
//        System.setProperty("opencell.keycloak.realm", "opencell");
//        System.setProperty("opencell.keycloak.clientId", "opencell-web");
//        System.setProperty("opencell.url", "http://localhost:8080");
//    }

    public static String getToken() {
        authenticateAsAdmin();
        return token;
    }
}
