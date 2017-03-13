package org.meveo.admin.web.filter.config;

public enum PrependType {

	AND, OR;

	public String value() {
		return name().toLowerCase();
	}

	public static PrependType fromValue(String value) {
		return valueOf(value);
	}

}
