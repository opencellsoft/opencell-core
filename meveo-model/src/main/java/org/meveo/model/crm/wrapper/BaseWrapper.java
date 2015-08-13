package org.meveo.model.crm.wrapper;

public abstract class BaseWrapper {
	protected static final String FORMAT = "%s=%s";
	protected static final String EQUAL = "=";
	protected static final String NULL = "null";
	protected static final int MAX_LENGTH=70;
	public abstract boolean isEmpty();
	public abstract boolean isNotEmpty();	
	public abstract String getLabel();
}
