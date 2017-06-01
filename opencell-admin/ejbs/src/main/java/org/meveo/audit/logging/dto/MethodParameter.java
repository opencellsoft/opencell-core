package org.meveo.audit.logging.dto;

import java.io.Serializable;

/**
 * @author Edward P. Legaspi
 **/
public class MethodParameter implements Serializable {

	private static final long serialVersionUID = -5420551690800886974L;

	private String name;
	private Object value;
	private String type;

	public MethodParameter() {

	}

	public MethodParameter(String name, Object value, String type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return getName().concat("$").concat(getType()).concat(":").concat(getValue().toString());
	}

}
