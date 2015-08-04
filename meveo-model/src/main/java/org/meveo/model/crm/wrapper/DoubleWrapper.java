package org.meveo.model.crm.wrapper;

import java.io.Serializable;


public class DoubleWrapper implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double doubleValue;
	private String label;
	public DoubleWrapper(){}
	public DoubleWrapper(Double doubleValue){
		this.doubleValue=doubleValue;
	}
	public DoubleWrapper(String label,Double doubleValue){
		this(doubleValue);
		this.label=label;
	}
	public Double getDoubleValue() {
		return doubleValue;
	}
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}