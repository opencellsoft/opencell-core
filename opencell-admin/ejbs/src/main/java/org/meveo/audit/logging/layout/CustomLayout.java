package org.meveo.audit.logging.layout;

import org.meveo.audit.logging.dto.AuditEvent;

/**
 * @author Edward P. Legaspi
 **/
public class CustomLayout implements Layout {

	private static final long serialVersionUID = 9077375694538927603L;

	@Override
	public String format(AuditEvent event) {
		return event.toString();
	}

}
