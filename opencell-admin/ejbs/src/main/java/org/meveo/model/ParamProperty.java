package org.meveo.model;

import org.slf4j.Logger;

import java.io.Serializable;


/**
 * @author Khalid HORRI
 * @lastModifiedVersion 5.2
 *
 */
public class ParamProperty implements Comparable<ParamProperty>, IEntity{

	public static final String PROPERTY_PATTERN = "^[a-zA-Z0-9_\\.]+$";

	private org.slf4j.Logger log;
	
	private String key;

	private String value;


	private String category;

	public ParamProperty(){

	}
	public ParamProperty(org.slf4j.Logger log){
		this.log=log;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		if(log!=null)
			log.debug("setKey :"+key);
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		if(log!=null)
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
		if(log != null)
			log.debug("setCategory :"+key+" -> "+category);
		this.category = category;
	}


	@Override
	public Serializable getId() {
		return null;
	}

	@Override
	public void setId(Long id) {

	}

	@Override
	public boolean isTransient() {
		return false;
	}
}
