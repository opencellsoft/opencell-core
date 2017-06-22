package org.meveo.model.payments;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue(value = "CARD")
@NamedQueries({
	@NamedQuery(name = "CardToken.updateDefaultToken", query = "UPDATE CardToken ct set ct.isDefault = false where ct.tokenId <> :defaultOne"),
	@NamedQuery(name = "CardToken.getDefaultToken", query = "SELECT ct FROM CardToken ct  WHERE ct.isDefault = true and ct.yearExpiration >:yearExpiration or (ct.monthExpiration >= :monthExpiration and ct.yearExpiration =:yearExpiration)"),
	@NamedQuery(name = "CardToken.getAvailableToken", query = "SELECT ct FROM CardToken ct  WHERE ct.yearExpiration >:yearExpiration or (ct.monthExpiration >= :monthExpiration and ct.yearExpiration =:yearExpiration)")
	
})
public class CardToken extends PaymentToken {

	private static final long serialVersionUID = 8726571628074346184L;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "card_type")
	@NotNull
	private CreditCardTypeEnum cardType;
	
	@Column(name = "owner")
	@NotNull
	private String owner;
	
	@Column(name = "month_expiration")
	@NotNull
	private Integer monthExpiration;
	
	@Column(name = "year_expiration")
	@NotNull
	private Integer yearExpiration;
	
	@Column(name = "card_number")
	@NotNull
	private String hiddenCardNumber;
		
	@Transient
	private String cardNumber;
	@Transient
	private String issueNumber;
	
	public CardToken(){		
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
	

	public String getHiddenCardNumber() {
		return hiddenCardNumber;
	}

	public void setHiddenCardNumber(String hiddenCardNumber) {
		this.hiddenCardNumber = hiddenCardNumber;
	}

	@Override
	public String toString() {
		return "CardToken [cardType=" + cardType + ", owner=" + owner + ", monthExpiration=" + monthExpiration
				+ ", yearExpiration=" + yearExpiration + ", hiddenCardNumber=" + hiddenCardNumber + ", cardNumber="
				+ cardNumber + ", issueNumber=" + issueNumber + "]";
	}

	
}
