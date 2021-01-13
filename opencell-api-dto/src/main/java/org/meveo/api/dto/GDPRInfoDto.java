package org.meveo.api.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GDPRInfoDto implements Serializable {

	private String key;
	private String value;
	
	public GDPRInfoDto(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public GDPRInfoDto() {
		
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
