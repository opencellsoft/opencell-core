package org.meveo.model.cpq.commercial;

/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
public enum OrderInvoiceStatusEnum{

	NEW("New"),
	DRAFT("Draft"),
	Validate("Validate");
	
	private String value;
	
	OrderInvoiceStatusEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}

}
