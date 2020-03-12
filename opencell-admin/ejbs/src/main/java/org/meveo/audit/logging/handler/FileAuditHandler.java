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
