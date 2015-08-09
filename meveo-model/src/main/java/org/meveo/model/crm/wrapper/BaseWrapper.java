package org.meveo.model.crm.wrapper;

public abstract class BaseWrapper {
	protected static final String FORMAT = "%s=%s";
	protected static final String EQUAL = "=";
	protected static final String NULL = "null";
//	protected static final int MAX_LENGTH=35;
	public abstract boolean isEmpty();
	public abstract boolean isNotEmpty();	
	public abstract String getLabel();
	public abstract void setLabel(String label);
	public abstract Object getValue();
	public abstract void setValue(Object value);
	public String getStringValue(){return null;}
	public void setStringValue(String stringValue){};
}
