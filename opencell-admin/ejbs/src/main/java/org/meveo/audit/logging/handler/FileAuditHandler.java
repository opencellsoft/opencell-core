package org.meveo.audit.logging.handler;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.dto.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * 
 *         jboss.server.log.dir
 * 
 *         Must add to standalone.xml
 * 
 *         <pre>
Handler	
&lt;periodic-rotating-file-handler name="APPLICATION-AUDIT" autoflush="true"&gt;
	&lt;formatter&gt;
		&lt;named-formatter name="AUDIT-PATTERN"/&gt;
	&lt;/formatter&gt;
	&lt;file relative-to="jboss.server.log.dir" path="application-audit.log"/&gt;
	&lt;suffix value=".yyyy-MM-dd"/&gt;
	&lt;append value="true"/&gt;
&lt;/periodic-rotating-file-handler&gt;

Logger
&lt;logger category="org.meveo.audit.logging.handler.FileAuditHandler"&gt;
	&lt;level name="INFO"/&gt;
	&lt;handlers&gt;
		&lt;handler name="APPLICATION-AUDIT"/&gt;
	&lt;/handlers&gt;
&lt;/logger&gt;

Make sure you also have the pattern defined:
&lt;formatter name="AUDIT-PATTERN"&gt;
    &lt;pattern-formatter pattern="%s%e%n"/&gt;
&lt;/formatter&gt;
 *         </pre>
 * 
 **/
public class FileAuditHandler extends Handler<AuditEvent> {
    /** logger.*/
	private static final Logger LOGGER = LoggerFactory.getLogger(FileAuditHandler.class);

	@Override
	public void handle() throws BusinessException {
		final String logText = getLoggableText();
		LOGGER.info(logText);
	}

}
