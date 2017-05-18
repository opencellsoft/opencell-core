package org.meveo.audit.logging.writer;

import java.util.Date;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.dto.AuditEvent;
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
		auditLog.setCreated(new Date());
		auditLog.setOrigin(auditEvent.getClientIp());
		auditLog.setParameters(auditEvent.getMethodParametersAsString());
		auditLog.setEntity(auditEvent.getEntity());

		create(auditLog);
	}

}
