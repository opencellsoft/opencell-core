package org.meveo.model.crm.wrapper;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class StringWrapper extends BaseWrapper implements Serializable{
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
	@Override
	public boolean isEmpty(){
		return StringUtils.isEmpty(label)&&StringUtils.isEmpty(stringValue);
	}
	@Override
	public boolean isNotEmpty(){
		return !isEmpty();
	}
	@Override
	public String toString() {
		if(StringUtils.isEmpty(label)){
			return StringUtils.isEmpty(stringValue)?NULL:stringValue;
		}else{
			return String.format(FORMAT, label,stringValue);
		}
		
	}
	public static StringWrapper parse(String value){
		if(value.indexOf(EQUAL)>0){
			String[] str=value.split(EQUAL);
			return new StringWrapper(str[0],NULL.equals(str[1])?null:str[1]);
		}else{
			return new StringWrapper(value);
		}
		
	}
	@Override
	public Object getValue() {
		return stringValue;
	}
	@Override
	public void setValue(Object value) {
		this.stringValue=(String)value;
	}
	
}
