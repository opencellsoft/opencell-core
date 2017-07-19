package org.meveo.api.dto.payment;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.CardToken;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.PaymentToken;

@XmlRootElement(name = "CardTokenRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardTokenDto extends BaseDto{
	private Long id;
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
	private Boolean isDisabled;
	private String userId;
	
	public CardTokenDto(){
		
	}

	public CardTokenDto(PaymentToken token) {
		this.id = token.getId();
		this.tokenId=token.getTokenId();
		this.isDefault=token.getIsDefault();
		this.customerAccountCode=token.getCustomerAccount().getCode();
		this.isDisabled=((CardToken)token).isDisabled();
		this.userId=token.getUserId();
		this.alias=token.getAlias();
		if(token instanceof CardToken){			
			this.cardType = ((CardToken)token).getCardType();
			this.owner=((CardToken)token).getOwner();
			this.monthExpiration=((CardToken)token).getMonthExpiration();
			this.yearExpiration=((CardToken)token).getYearExpiration();
			this.cardNumber=((CardToken)token).getHiddenCardNumber();
			this.issueNumber=((CardToken)token).getIssueNumber();

		}
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
	
	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "CardTokenRequestDto [id="+id+",isDisabled="+isDisabled+", cardType=" + cardType + ", owner=" + owner + ", monthExpiration=" + monthExpiration
				+ ", yearExpiration=" + yearExpiration + ", tokenId=" + tokenId + ", alias=" + alias + ", isDefault="
				+ isDefault + ", customerAccountCode=" + customerAccountCode + ", cardNumber=" + ((cardNumber != null && cardNumber.length() == 16)?cardNumber.substring(12, 15) : "invalid")
				+ ", issueNumber=" + issueNumber + ",userId="+userId+"]";
	}

	
}
