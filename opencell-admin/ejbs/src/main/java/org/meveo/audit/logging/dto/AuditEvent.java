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

package org.meveo.audit.logging.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward P`. Legaspi
 **/
public class AuditEvent extends Event {

	private String actor;
	private String clientIp;
	private String entity;
	private String action;
	/** The action item. */
	private List<MethodParameter> fields = new ArrayList<MethodParameter>();

	public AuditEvent() {

	}

	public AuditEvent(final String actor, final String clientIp, final String entity, final String action,
			MethodParameter... fields) {
		this.actor = actor;
		this.clientIp = clientIp;
		this.entity = entity;
		this.action = action;

		for (MethodParameter field : fields) {
			addField(field);
		}
	}

	public void addField(String name, Object value, Object type) {
		if (value == null) {
			this.fields.add(new MethodParameter(name, null, null));
		} else {
			this.fields.add(new MethodParameter(name, value.toString(), type.toString()));
		}
	}

	public void addField(final String name, final Object value) {
		if (value == null) {
			this.fields.add(new MethodParameter(name, null, null));
		} else {
			this.fields.add(new MethodParameter(name, value.toString(), value.getClass().getName()));
		}
	}

	public String getMethodParametersAsString() {
		StringBuilder sb = new StringBuilder();
		if (getFields() != null && !getFields().isEmpty()) {
			for (MethodParameter mp : getFields()) {
				sb.append(mp.toString() + ", ");
			}
			sb.delete(sb.length() - 2, sb.length() - 1);
		}

		return sb.toString();
	}

	public void addField(final MethodParameter field) {
		this.fields.add(field);
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<MethodParameter> getFields() {
		return fields;
	}

	public void setFields(List<MethodParameter> fields) {
		this.fields = fields;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	@Override
	public String toString() {
		return "AuditEvent [actor=" + actor + ", clientIp=" + clientIp + ", entity=" + entity + ", action=" + action
				+ ", fields=" + fields + "]";
	}

}
