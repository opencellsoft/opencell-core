package org.meveo.model.crm.wrapper;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class LongWrapper extends BaseWrapper implements Serializable{
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
	@Override
	public boolean isEmpty(){
		return StringUtils.isEmpty(label)&&longValue==null;
	}
	@Override
	public boolean isNotEmpty(){
		return !isEmpty();
	}
	@Override
	public String toString() {
		if(StringUtils.isEmpty(label)){
			return String.valueOf(longValue);
		}else{
			return String.format(FORMAT, label,longValue==null?NULL:String.valueOf(longValue));
		}
		
	}
	public static LongWrapper parse(String value){
		if(value.indexOf(EQUAL)>0){
			String[] str=value.split(EQUAL);
			return new LongWrapper(str[0],NULL.equals(str[1])?null:Long.parseLong(str[1]));
		}else{
			return new LongWrapper(Long.valueOf(value));
		}
	}
	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setValue(Object value) {
		// TODO Auto-generated method stub
		
	}
	
}