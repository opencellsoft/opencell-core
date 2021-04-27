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

package org.meveo.security;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

/**
 * @author Edward P. Legaspi
 * 
 **/
@SessionScoped
public class UserAuthTimeProducer implements Serializable {

    private static final long serialVersionUID = 5510518807024231791L;

    /**
     * Timestamp when user has authenticated or token was issued
     */
    protected int authenticatedAt;

    /**
     * Authentication/session token hash/id
     */
    protected String authenticationTokenId;

    /**
     * @return Timestamp when user has authenticated or token was issued
     */
    public int getAuthenticatedAt() {
        return authenticatedAt;
    }

    /**
     * @param authenticatedAt Timestamp when user has authenticated or token was issued
     */
    public void setAuthenticatedAt(int authenticatedAt) {
        this.authenticatedAt = authenticatedAt;
    }

    /**
     * @return Authentication/session token hash/id
     */
    public String getAuthenticationTokenId() {
        return authenticationTokenId;
    }

    /**
     * @param authenticationTokenId Authentication/session token hash/id
     */
    public void setAuthenticationTokenId(String authenticationTokenId) {
        this.authenticationTokenId = authenticationTokenId;
    }
}