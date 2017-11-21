package org.meveo.keycloak.client;

/**
 * @author Edward P. Legaspi
 * @created 10 Nov 2017
 **/
public class KeycloakAdminClientConfig {

    private String serverUrl;
    private String realm;
    private String clientId;
    private String clientSecret;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public String toString() {
        return "KeycloakAdminClientConfig [serverUrl=" + serverUrl + ", realm=" + realm + ", clientId=" + clientId + ", clientSecret=" + clientSecret + "]";
    }

}
