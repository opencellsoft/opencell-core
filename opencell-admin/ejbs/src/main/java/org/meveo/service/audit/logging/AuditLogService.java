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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.audit.logging.AuditLog;
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

    public <T extends BaseEntity> void trackOperation(String operationType, Date operationDate, T entity, String origine) {
        trackOperation(operationType, operationDate, entity, origine, new ArrayList<>());
    }

    public <T extends BaseEntity> void trackOperation(String operationType, Date operationDate, T entity, String origine, List<String> fields) {
        String parameters = getDefaultMessage(operationType, operationDate, entity, origine, fields);
        trackOperation(operationType, operationDate, entity, origine, parameters);
    }

	public <T extends BaseEntity> String getDefaultMessage(String operationType, Date operationDate, T entity, String origine, List<String> fields) {
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss");
        StringBuilder parameters = new StringBuilder()
                .append(formatter.format(operationDate))
                .append(" - ").append(getActor()).append(" - ")
                .append(" applied ").append(operationType)
                .append(" to ").append(entity.getClass().getSimpleName())
                .append(" with ").append(origine)
                .append(fields != null && !fields.isEmpty() ? ", fields (" + String.join(",", fields) + ")" : "");
		return parameters.toString();
	}

    public <T extends BaseEntity> void trackOperation(String operationType, Date operationDate, T entity, String source, String parameters) {
        String actor = getActor();
        String simpleName = entity.getClass().getSimpleName();

        AuditLog auditLog = new AuditLog();
        auditLog.setEntity(simpleName);
        auditLog.setCreated(operationDate);
        auditLog.setActor(actor);
        auditLog.setAction(operationType);
        auditLog.setParameters(parameters);
        auditLog.setOrigin(entity.getId() != null ? String.valueOf(entity.getId()) : null);
        auditLog.setSource(source);
        create(auditLog);
    }

    public String getActor() {
        if (StringUtils.isNotBlank(currentUser.getFullName())) {
            return currentUser.getFullName();
        } else if (StringUtils.isNotBlank(currentUser.getEmail())) {
            return currentUser.getEmail();
        }
        return currentUser.getUserName();
    }

    /**
     * Purge audit logs older than purge date.
     *
     * @param purgeDate the purge date
     * @return Number of records deleted
     */
    public int purgeAuditLog(Date purgeDate) {
        return getEntityManager().createNamedQuery("AuditLog.purgeAuditLog").setParameter("purgeDate", purgeDate).executeUpdate();
    }
}
