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

package org.meveo.audit.logging.writer;

import java.util.Date;

import jakarta.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.dto.AuditEvent;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AuditEventDBWriter extends PersistenceService<AuditLog> {

	public void write(AuditEvent auditEvent) throws BusinessException {
		AuditLog auditLog = new AuditLog();
		auditLog.setAction(auditEvent.getAction());
		auditLog.setActor(auditEvent.getActor());

		// temp fix
		if (StringUtils.isBlank(auditLog.getActor())) {
			auditLog.setActor("opencell.admin.hardcoded");
		}

		auditLog.setCreated(new Date());
		auditLog.setOrigin(auditEvent.getClientIp());
		auditLog.setParameters(auditEvent.getMethodParametersAsString());
		auditLog.setEntity(auditEvent.getEntity());

		create(auditLog);
	}

}
