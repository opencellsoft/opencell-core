package org.meveo.model;

public class ParamProperty {

	private org.slf4j.Logger log;
	
	private String key;
	
	private String value;
	
	public ParamProperty(org.slf4j.Logger log){
		this.log=log;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		log.debug("setKey :"+key);
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		log.debug("setValue :"+key+" -> "+value);
		this.value = value;
	}
	
	
}
