package org.meveo.api.dto.payment;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CreditCardTypeEnum;

@XmlRootElement(name = "DoPaymentRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class DoPaymentRequestDto extends BaseDto{
	private String customerAccountCode;
	private Long ctsAmount;
	private String cardNumber;
	private String ownerName;
	private String cvv;
	private String expirayDate;
	private CreditCardTypeEnum cardType;
	private List<Long> aoToPay;
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

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getExpirayDate() {
		return expirayDate;
	}

	public void setExpirayDate(String expirayDate) {
		this.expirayDate = expirayDate;
	}

	public CreditCardTypeEnum getCardType() {
		return cardType;
	}

	public void setCardType(CreditCardTypeEnum cardType) {
		this.cardType = cardType;
	}

	public List<Long> getAoToPay() {
		return aoToPay;
	}

	public void setAoToPay(List<Long> aoToPay) {
		this.aoToPay = aoToPay;
	}

	@Override
	public String toString() {
		return "DoPaymentRequestDto [customerAccountCode=" + customerAccountCode + ", ctsAmount=" + ctsAmount
				+ ", cardNumber=" + StringUtils.hideCardNumber(cardNumber) + ", ownerName=" + ownerName + ", cvv=" + cvv + ", expirayDate="
				+ expirayDate + ", cardType=" + cardType + ", aoToPay=" + aoToPay + ", comment=" + comment
				+ ", createAO=" + createAO + ", isToMatching=" + isToMatching + "]";
	}

	
}
