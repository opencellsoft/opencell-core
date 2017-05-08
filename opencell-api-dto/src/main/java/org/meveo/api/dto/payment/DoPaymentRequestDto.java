package org.meveo.api.dto.payment;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

@XmlRootElement(name = "DoPaymentRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class DoPaymentRequestDto extends BaseDto{
	private String customerAccountCode;
	private Long ctsAmount;
	private String invoiceNumber;
	private String comment;
	private boolean createAO;
	private boolean isToMatching;
	
	public DoPaymentRequestDto(){
		
	}

	public String getCustomerAccountCode() {
		return customerAccountCode;
	}

	public void setCustomerAccountCode(String customerAccountCode) {
		this.customerAccountCode = customerAccountCode;
	}

	public Long getCtsAmount() {
		return ctsAmount;
	}

	public void setCtsAmount(Long ctsAmount) {
		this.ctsAmount = ctsAmount;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	

	public boolean isCreateAO() {
		return createAO;
	}

	public void setCreateAO(boolean createAO) {
		this.createAO = createAO;
	}

	public boolean isToMatching() {
		return isToMatching;
	}

	public void setToMatching(boolean isToMatching) {
		this.isToMatching = isToMatching;
	}

	@Override
	public String toString() {
		return "DoPaymentRequestDto [customerAccountCode=" + customerAccountCode + ", ctsAmount=" + ctsAmount
				+ ", invoiceNumber=" + invoiceNumber + ", comment=" + comment + ", createAO=" + createAO
				+ ", isToMatching=" + isToMatching + "]";
	}	
}
