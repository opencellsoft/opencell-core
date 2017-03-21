package org.meveo.model;

public class ParamProperty implements Comparable<ParamProperty>{

	private org.slf4j.Logger log;
	
	private String key;

	private String value;
	
	private String category;
	
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

	@Override
	public int compareTo(ParamProperty arg0) {
		int result=0;
		if(arg0!=null){
			result=this.key.compareTo(arg0.key);
		}
		return result;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		log.debug("setCategory :"+key+" -> "+category);
		this.category = category;
	}	
	
}
