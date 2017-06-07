package org.meveo.audit.logging.layout;

import java.util.Date;

import org.meveo.audit.logging.core.AuditConstants;
import org.meveo.audit.logging.dto.AuditEvent;
import org.meveo.audit.logging.dto.MethodParameter;
import org.meveo.model.shared.DateUtils;

/**
 * @author Edward P. Legaspi
 **/
public class SimpleLayout implements Layout {

	private static final long serialVersionUID = 8118062662785065233L;

	@Override
	public String format(AuditEvent event) {
		final StringBuilder sb = new StringBuilder();

		if (null != event.getCreated()) {
			sb.append(DateUtils.formatDateWithPattern(event.getCreated(), DateUtils.DATE_TIME_PATTERN));
		} else {
			sb.append(DateUtils.formatDateWithPattern(new Date(), DateUtils.DATE_TIME_PATTERN));
		}
		sb.append(AuditConstants.SEPARATOR);

		sb.append(event.getActor());
		sb.append(AuditConstants.SEPARATOR);
		sb.append(event.getClientIp());
		sb.append(AuditConstants.SEPARATOR);
		sb.append(event.getEntity());
		sb.append(AuditConstants.SEPARATOR);

		if (event.getAction() != null) {
			sb.append(event.getAction());
			sb.append(AuditConstants.ARROW);
		}
		if (event.getFields() != null && !event.getFields().isEmpty()) {
			for (MethodParameter methodParam : event.getFields()) {
				sb.append(methodParam.getName()).append(AuditConstants.COLON).append(methodParam.getType())
						.append(AuditConstants.COLON).append(methodParam.getValue()).append(AuditConstants.COMMA);
			}
		}

		return sb.toString();
	}

}
