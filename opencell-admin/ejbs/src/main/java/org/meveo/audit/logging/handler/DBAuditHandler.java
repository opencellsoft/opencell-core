package org.meveo.audit.logging.handler;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.dto.AuditEvent;
import org.meveo.audit.logging.writer.AuditEventDBWriter;
import org.meveo.commons.utils.EjbUtils;

/**
 * @author Edward P. Legaspi
 **/
public class DBAuditHandler extends Handler<AuditEvent> {

	@Override
	public void handle() throws BusinessException {
		AuditEventDBWriter auditEventDBWriter = ((AuditEventDBWriter) EjbUtils
				.getServiceInterface("AuditEventDBWriter"));

		auditEventDBWriter.write(getEvent());
	}

}
