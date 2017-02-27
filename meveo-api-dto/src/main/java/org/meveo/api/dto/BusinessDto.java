package org.meveo.api.dto;


/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
public abstract class BusinessDto extends BaseDto {

	private static final long serialVersionUID = 4451119256601996946L;

	private String currentCode;

	public String getCurrentCode() {
		return currentCode;
	}

	public void setCurrentCode(String currentCode) {
		this.currentCode = currentCode;
	}
	
	
}
