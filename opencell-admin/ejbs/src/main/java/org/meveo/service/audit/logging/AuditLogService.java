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

package org.meveo.service.audit.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.dunning.DunningCollectionPlan;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AuditLogService extends PersistenceService<AuditLog> {

    /**
     * purge audit logs.
     */
    public void purge() {
        String hqlQuery = String.format("delete from AuditLog");
        getEntityManager().createQuery(hqlQuery).executeUpdate();
    }

    public <T extends BaseEntity> void trackOperation(String origin, String operationType, Date operationDate, T entity) {
        trackOperation(origin, operationType, operationDate, entity, null);
    }

    public <T extends BaseEntity> void trackOperation(String origin, String operationType, Date operationDate, T entity, List<String> fields) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss");
        String actor = getActor();
        AuditLog auditLog = new AuditLog();
        auditLog.setEntity(DunningCollectionPlan.class.getSimpleName());
        auditLog.setCreated(operationDate);
        auditLog.setActor(getActor());
        auditLog.setAction(operationType);
        StringBuilder parameters = new StringBuilder()
                .append(formatter.format(operationDate))
                .append(" - ").append(actor).append(" - ")
                .append(" apply ").append(operationType)
                .append(" to ").append(entity.getClass().getSimpleName()).append(" with ").append(getCodeOrId(entity))
                .append(fields != null && !fields.isEmpty() ? ", fields (" + String.join(",", fields) + ")" : "");
        auditLog.setParameters(parameters.toString());
        auditLog.setOrigin(origin);
        create(auditLog);
    }

    private String getActor() {
        if (StringUtils.isNotBlank(currentUser.getFullNameOrUserName())) {
            return currentUser.getFullNameOrUserName();
        } else if (StringUtils.isNotBlank(currentUser.getEmail())) {
            return currentUser.getEmail();
        }
        return currentUser.getUserName();
    }

    private <T extends BaseEntity> String getCodeOrId(T entity) {
        String codeOrId = null;
        if (entity instanceof BusinessEntity) {
            codeOrId = ((BusinessEntity) entity).getCode();
        }
        if (StringUtils.isNotBlank(codeOrId)) {
            return "code " + codeOrId;
        }
        return "id " + entity.getId();
    }
}
