package org.meveo.audit.logging.layout;

import org.meveo.audit.logging.dto.AuditEvent;

/**
 * @author Edward P. Legaspi
 **/
public class SimpleLayout implements Layout {

	private static final long serialVersionUID = 8118062662785065233L;

	@Override
	public String format(AuditEvent event) {
		return event.toString();
	}

}
