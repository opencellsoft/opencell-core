package com.opencell.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.HttpClients;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.Before;
import cucumber.runtime.CucumberException;
import io.restassured.RestAssured;

public class KeyCloakAuthenticationHook {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private static Map<String,String> tokens = new HashMap<>();
    private static String token;


    @Before("@admin")
    public void authenticateAsAdmin() {
        // setProperties();// TODO to be removed after test, just for debug
        String adminUser = System.getProperty("adminUsername");
        String adminPassword = System.getProperty("adminPassword");
        setToken(adminUser,adminPassword);
    }


    @Before("@superAdmin")
    public void authenticateAsSuperAdmin() {
        // setProperties();// TODO to be removed after test, just for debug
        String superAdminUser = System.getProperty("superUsername");
        String superAdminPassword = System.getProperty("superPassword");
        setToken(superAdminUser,superAdminPassword);
    }

    private void setToken(String login, String password) {
        if (token == null) {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
            if (tokens.get(login) != null) {
                token = tokens.get(login);
                return;
            }
            Map<String, Object> clientCredentials = new HashMap<>();
            clientCredentials.put("secret", System.getProperty("opencell.keycloak.secret"));
            Configuration config = new Configuration(System.getProperty("opencell.keycloak.url"),
                    System.getProperty("opencell.keycloak.realm"), System.getProperty("opencell.keycloak.clientId"),
                    clientCredentials, HttpClients.createDefault());
            AuthzClient authzClient = AuthzClient.create(config);
            AccessTokenResponse response = authzClient.obtainAccessToken(login, password);
            if (response.getToken() != null) {
                token = response.getToken();
                tokens.put(login, token);
            } else {
                throw new CucumberException("Could not acquire the KC token, please check if the KC is running");
            }
        }
    }

    private void setProperties() {
        System.setProperty("adminUsername","opencell.admin");
        System.setProperty("adminPassword","opencell.admin");
        System.setProperty("superUsername","opencell.superadmin");
        System.setProperty("superPassword","opencell.superadmin");
        System.setProperty("opencell.keycloak.secret","afe07e5a-68cb-4fb0-8b75-5b6053b07dc3");
        System.setProperty("opencell.keycloak.url","http://localhost:8080/auth");
        System.setProperty("opencell.keycloak.realm","opencell");
        System.setProperty("opencell.keycloak.clientId","opencell-web");
        System.setProperty("opencell.url","http://localhost:8080/opencell");
    }




    public static String getToken() {
        return token;
    }
}
