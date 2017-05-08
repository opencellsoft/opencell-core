package org.meveo.api.dto.payment;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.CreditCardTypeEnum;

@XmlRootElement(name = "CardTokenRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardTokenRequestDto extends BaseDto{
	private CreditCardTypeEnum cardType;
	private String owner;
	private Integer monthExpiration;
	private Integer yearExpiration;
	private String tokenId;
	private String alias;
	private Boolean isDefault;
	private String customerAccountCode;
	private String cardNumber;
	private String issueNumber;
	
	public CardTokenRequestDto(){
		
	}

	public CreditCardTypeEnum getCardType() {
		return cardType;
	}

	public void setCardType(CreditCardTypeEnum cardType) {
		this.cardType = cardType;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getMonthExpiration() {
		return monthExpiration;
	}

	public void setMonthExpiration(Integer monthExpiration) {
		this.monthExpiration = monthExpiration;
	}

	public Integer getYearExpiration() {
		return yearExpiration;
	}

	public void setYearExpiration(Integer yearExpiration) {
		this.yearExpiration = yearExpiration;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getCustomerAccountCode() {
		return customerAccountCode;
	}

	public void setCustomerAccountCode(String customerAccountCode) {
		this.customerAccountCode = customerAccountCode;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getIssueNumber() {
		return issueNumber;
	}

	public void setIssueNumber(String issueNumber) {
		this.issueNumber = issueNumber;
	}

	@Override
	public String toString() {
		return "CardTokenRequestDto [cardType=" + cardType + ", owner=" + owner + ", monthExpiration=" + monthExpiration
				+ ", yearExpiration=" + yearExpiration + ", tokenId=" + tokenId + ", alias=" + alias + ", isDefault="
				+ isDefault + ", customerAccountCode=" + customerAccountCode + ", cardNumber=" + ((cardNumber != null && cardNumber.length() == 16)?cardNumber.substring(12, 15) : "invalid")
				+ ", issueNumber=" + issueNumber + "]";
	}

	
}
