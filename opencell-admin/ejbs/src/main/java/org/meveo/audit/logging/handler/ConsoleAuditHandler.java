package org.meveo.audit.logging.handler;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.dto.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * 
 *         Uses slf4j to log in console.
 **/
public class ConsoleAuditHandler extends Handler<AuditEvent> {

	private static Logger LOGGER = LoggerFactory.getLogger(ConsoleAuditHandler.class);

	@Override
	public void handle() throws BusinessException {
		final String logText = getLoggableText();
		LOGGER.info(logText);
	}

}
