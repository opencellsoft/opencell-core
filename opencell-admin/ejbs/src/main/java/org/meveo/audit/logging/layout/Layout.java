package org.meveo.audit.logging.layout;

import java.io.Serializable;

import org.meveo.audit.logging.dto.AuditEvent;

/**
 * @author Edward P. Legaspi
 **/
public interface Layout extends Serializable {

	String format(AuditEvent event);

}
