package org.meveo.audit.logging.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward P`. Legaspi
 **/
public class AuditEvent {

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

}
