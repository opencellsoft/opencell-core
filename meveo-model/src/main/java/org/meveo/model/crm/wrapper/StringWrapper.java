package org.meveo.model.crm.wrapper;

import java.io.Serializable;

public class StringWrapper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stringValue;
	private String label;
	public StringWrapper(){}
	public StringWrapper(String stringValue){
		this.stringValue=stringValue;
	}
	public StringWrapper(String label,String stringValue){
		this(stringValue);
		this.label=label;
	}
	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
