package org.meveo.client;

/**
 * @author anasseh
 *
 *
 *   JSON  : application/json
 *   XML   : application/xml
 *   TEXT  : text/html
 *   
 *   For other contents type set it on the header 
 */
public enum CommonContentTypeEnum {
	JSON("application/json"), XML("application/xml"),TEXT("text/html");
	
	private String value;
	
	CommonContentTypeEnum(String val){
		this.value = val;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
