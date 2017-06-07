package org.meveo.audit.logging.handler;

import org.meveo.admin.exception.BusinessException;
import org.meveo.audit.logging.dto.AuditEvent;

/**
 * @author Edward P. Legaspi
 **/
public abstract class Handler<T extends AuditEvent> {

	private String loggableText;
	private T event;

	public abstract void handle() throws BusinessException;

	public String getLoggableText() {
		return loggableText;
	}

	public void setLoggableText(String loggableText) {
		this.loggableText = loggableText;
	}

	public T getEvent() {
		return event;
	}

	public void setEvent(T event) {
		this.event = event;
	}

}
