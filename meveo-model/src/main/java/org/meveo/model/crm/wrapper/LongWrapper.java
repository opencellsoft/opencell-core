package org.meveo.model.crm.wrapper;

import java.io.Serializable;

public class LongWrapper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long longValue;
	private String label;
	public LongWrapper(){}
	public LongWrapper(Long longValue){
		this.longValue=longValue;
	}
	public LongWrapper(String label,Long longValue){
		this(longValue);
		this.label=label;
	}
	public Long getLongValue() {
		return longValue;
	}
	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}