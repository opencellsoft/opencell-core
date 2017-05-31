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
<periodic-rotating-file-handler name="APPLICATION-AUDIT" autoflush="true">
	<formatter>
		<named-formatter name="PATTERN"/>
	</formatter>
	<file relative-to="jboss.server.log.dir" path="application-audit.log"/>
	<suffix value=".yyyy-MM-dd"/>
	<append value="true"/>
</periodic-rotating-file-handler>

Logger
<logger category="org.meveo.audit.logging.handler.FileAuditHandler">
	<level name="INFO"/>
	<handlers>
		<handler name="APPLICATION-AUDIT"/>
	</handlers>
</logger>

Make sure you also have the pattern defined:
<formatter name="PATTERN">
	<pattern-formatter pattern=
"%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n"/>
</formatter>
 *         </pre>
 * 
 **/
public class FileAuditHandler extends Handler<AuditEvent> {

	private static Logger LOGGER = LoggerFactory.getLogger(FileAuditHandler.class);

	@Override
	public void handle() throws BusinessException {
		final String logText = getLoggableText();
		LOGGER.info(logText);
	}

}
