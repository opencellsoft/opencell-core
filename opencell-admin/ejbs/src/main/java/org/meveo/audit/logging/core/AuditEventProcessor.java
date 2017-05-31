package org.meveo.audit.logging.core;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.dto.AuditEvent;
import org.meveo.audit.logging.handler.Handler;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AuditEventProcessor {

	public void process(AuditEvent auditEvent) throws BusinessException {
		final String formattedEvent = AuditContext.getInstance().getAuditConfiguration().getLayout().format(auditEvent);

		for (final Handler handler : AuditContext.getInstance().getAuditConfiguration().getHandlers()) {
			handler.setLoggableText(formattedEvent);
			handler.setEvent(auditEvent);
			handler.handle();
		}
	}

}
