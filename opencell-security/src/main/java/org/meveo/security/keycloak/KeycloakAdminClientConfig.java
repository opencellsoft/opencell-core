/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.security.keycloak;

/**
 * Keycloak connection configuration
 * 
 * @author Edward P. Legaspi
 * @since 10 Nov 2017
 **/
public class KeycloakAdminClientConfig {

    /**
     * Keycloak server url
     */
    private String serverUrl;

    /**
     * Realm name
     */
    private String realm;

    /**
     * Client name
     */
    private String clientName;

    /**
     * Client identifier
     */
    private String clientId;

    /**
     * Client secret
     */
    private String clientSecret;

    /**
     * @return Keycloak server url
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * @param serverUrl Keycloak server url
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * @return Client name
     */
    public String getRealm() {
        return realm;
    }

    /**
     * @param realm Client name
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * @return Client name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * @param clientName Client name
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * @return Client identifier
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId Client identifier
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return Client secret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @param clientSecret Client secret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public String toString() {
        return "KeycloakAdminClientConfig [serverUrl=" + serverUrl + ", realm=" + realm + ", clientId=" + clientName + ", clientSecret=" + clientSecret + "]";
    }

}
