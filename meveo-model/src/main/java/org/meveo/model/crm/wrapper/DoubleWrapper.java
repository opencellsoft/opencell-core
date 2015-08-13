package org.meveo.model.crm.wrapper;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;


public class DoubleWrapper extends BaseWrapper implements Serializable{
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
	@Override
	public boolean isEmpty(){
		return StringUtils.isEmpty(label)&&doubleValue==null;
	}
	@Override
	public boolean isNotEmpty(){
		return !isEmpty();
	}
	@Override
	public String toString() {
		if(StringUtils.isEmpty(label)){
			return String.valueOf(doubleValue);
		}else{
			return String.format(FORMAT, label,doubleValue==null?NULL:String.valueOf(doubleValue));
		}
		
	}
	public static DoubleWrapper parse(String value){
		if(value.indexOf(EQUAL)>0){
			String[] str=value.split(EQUAL);
			return new DoubleWrapper(str[0],NULL.equals(str[1])?null:Double.parseDouble(str[1]));
		}else{
			return new DoubleWrapper(Double.parseDouble(value));
		}
	}
}