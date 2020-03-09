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
