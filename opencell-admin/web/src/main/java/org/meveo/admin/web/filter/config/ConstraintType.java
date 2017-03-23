package org.meveo.admin.web.filter.config;

public enum ConstraintType {

	READ_WRITE, READ, WRITE;

	public String value() {
		return name();
	}

	public static ConstraintType fromValue(String value) {
		return valueOf(value);
	}
}