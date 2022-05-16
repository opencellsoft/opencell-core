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
package org.meveo.admin.action.admin;

import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.security.client.ApiProtectionGranularityEnum;
import org.meveo.security.client.KeycloakAdminClientService;

@Named
@ConversationScoped
public class ApiSecurityBean implements Serializable {

    private static final long serialVersionUID = 5761298784298195322L;

    /**
     * Protect API granularity level for find/search access
     */
    ApiProtectionGranularityEnum readLevel = null;

    /**
     * Protect API granularity level for create access
     */
    ApiProtectionGranularityEnum createLevel = null;

    /**
     * Protect API granularity level for update access
     */
    ApiProtectionGranularityEnum updateLevel = null;

    /**
     * Protect API granularity level for delete access
     */
    ApiProtectionGranularityEnum deleteLevel = null;

    @Inject
    protected Messages messages;

    @Inject
    protected KeycloakAdminClientService keycloakAdminClientService;

    public ApiProtectionGranularityEnum[] getApiLevels() {
        return ApiProtectionGranularityEnum.values();
    }

    @ActionMethod
    public void syncApiRoles() {

        keycloakAdminClientService.syncApiProtection(readLevel, createLevel, updateLevel, deleteLevel);
        messages.info(new BundleKey("messages", "roles.apiRolesGenerated"));
    }

    public ApiProtectionGranularityEnum getReadLevel() {
        return readLevel;
    }

    public void setReadLevel(ApiProtectionGranularityEnum readLevel) {
        this.readLevel = readLevel;
    }

    public ApiProtectionGranularityEnum getCreateLevel() {
        return createLevel;
    }

    public void setCreateLevel(ApiProtectionGranularityEnum createLevel) {
        this.createLevel = createLevel;
    }

    public ApiProtectionGranularityEnum getUpdateLevel() {
        return updateLevel;
    }

    public void setUpdateLevel(ApiProtectionGranularityEnum updateLevel) {
        this.updateLevel = updateLevel;
    }

    public ApiProtectionGranularityEnum getDeleteLevel() {
        return deleteLevel;
    }

    public void setDeleteLevel(ApiProtectionGranularityEnum deleteLevel) {
        this.deleteLevel = deleteLevel;
    }
}