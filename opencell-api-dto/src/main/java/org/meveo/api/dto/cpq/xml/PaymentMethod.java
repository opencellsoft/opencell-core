package org.meveo.api.dto.cpq.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;

@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentMethod {

	private PaymentMethodEnum tokenType;
	private CreditCardTypeEnum cardType;
	private String owner;
	private int yearExpiration;
	private int monthExpiration;
	private String alias;
	private String cardNumber;
	private String bankCode;
	private String bankAccountNumber;
	private String bankBranchCode;
	private String hashKey;
	private String iban;
	private String bic; 
	private String accountOwner;
	private String bankName;
	private String ics;
	private String issuerName;
	private Date mandateDate;
	private String mandateIdentification;
	private String info1;
	private String info2;
	private String info3;
	private String info4;
	private String info5;
	private CustomFieldDto customField;
	
	public PaymentMethod(org.meveo.model.payments.PaymentMethod p, CustomFieldDto customField) {
		this.tokenType = p.getPaymentType();
		if(p instanceof CardPaymentMethod) {
			CardPaymentMethod cpm = (CardPaymentMethod) p;
			this.cardType = cpm.getCardType();
			this.owner = cpm.getOwner();
			this.yearExpiration = cpm.getYearExpiration();
			this.monthExpiration = cpm.getMonthExpiration();
			this.cardNumber = cpm.getCardNumber();
		}else if(p instanceof DDPaymentMethod) {
			DDPaymentMethod ddp = (DDPaymentMethod) p;
			var bcs = ddp.getBankCoordinates();
			this.bankCode = bcs.getBankCode();
			this.bankAccountNumber = bcs.getAccountNumber();
			this.bankBranchCode = bcs.getBranchCode();
			this.iban = bcs.getIban();
			this.bic = bcs.getBic();
			this.accountOwner = bcs.getAccountOwner();
			this.ics = bcs.getIcs();
			this.issuerName = bcs.getIssuerName();
			this.mandateDate = ddp.getMandateDate();
			this.mandateIdentification = ddp.getMandateIdentification();
		}
		this.hashKey = p.getTokenId();
		this.info1 = p.getInfo1();
		this.info2 = p.getInfo2();
		this.info3 = p.getInfo3();
		this.info4 = p.getInfo4();
		this.info5 = p.getInfo5();
		this.alias = p.getAlias();
		this.customField = customField;
		
	}
	
	/**
	 * @return the tokenType
	 */
	public PaymentMethodEnum getTokenType() {
		return tokenType;
	}
	/**
	 * @param tokenType the tokenType to set
	 */
	public void setTokenType(PaymentMethodEnum tokenType) {
		this.tokenType = tokenType;
	}
	/**
	 * @return the cardType
	 */
	public CreditCardTypeEnum getCardType() {
		return cardType;
	}
	/**
	 * @param cardType the cardType to set
	 */
	public void setCardType(CreditCardTypeEnum cardType) {
		this.cardType = cardType;
	}
	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	/**
	 * @return the yearExpiration
	 */
	public int getYearExpiration() {
		return yearExpiration;
	}
	/**
	 * @param yearExpiration the yearExpiration to set
	 */
	public void setYearExpiration(int yearExpiration) {
		this.yearExpiration = yearExpiration;
	}
	/**
	 * @return the monthExpiration
	 */
	public int getMonthExpiration() {
		return monthExpiration;
	}
	/**
	 * @param monthExpiration the monthExpiration to set
	 */
	public void setMonthExpiration(int monthExpiration) {
		this.monthExpiration = monthExpiration;
	}
	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}
	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}
	/**
	 * @param cardNumber the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	/**
	 * @return the bankCode
	 */
	public String getBankCode() {
		return bankCode;
	}
	/**
	 * @param bankCode the bankCode to set
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	/**
	 * @return the bankAccountNumber
	 */
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
	/**
	 * @param bankAccountNumber the bankAccountNumber to set
	 */
	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}
	/**
	 * @return the bankBranchCode
	 */
	public String getBankBranchCode() {
		return bankBranchCode;
	}
	/**
	 * @param bankBranchCode the bankBranchCode to set
	 */
	public void setBankBranchCode(String bankBranchCode) {
		this.bankBranchCode = bankBranchCode;
	}
	/**
	 * @return the hashKey
	 */
	public String getHashKey() {
		return hashKey;
	}
	/**
	 * @param hashKey the hashKey to set
	 */
	public void setHashKey(String hashKey) {
		this.hashKey = hashKey;
	}
	/**
	 * @return the iban
	 */
	public String getIban() {
		return iban;
	}
	/**
	 * @param iban the iban to set
	 */
	public void setIban(String iban) {
		this.iban = iban;
	}
	/**
	 * @return the bic
	 */
	public String getBic() {
		return bic;
	}
	/**
	 * @param bic the bic to set
	 */
	public void setBic(String bic) {
		this.bic = bic;
	}
	/**
	 * @return the accountOwner
	 */
	public String getAccountOwner() {
		return accountOwner;
	}
	/**
	 * @param accountOwner the accountOwner to set
	 */
	public void setAccountOwner(String accountOwner) {
		this.accountOwner = accountOwner;
	}
	/**
	 * @return the bankName
	 */
	public String getBankName() {
		return bankName;
	}
	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	/**
	 * @return the ics
	 */
	public String getIcs() {
		return ics;
	}
	/**
	 * @param ics the ics to set
	 */
	public void setIcs(String ics) {
		this.ics = ics;
	}
	/**
	 * @return the issuerName
	 */
	public String getIssuerName() {
		return issuerName;
	}
	/**
	 * @param issuerName the issuerName to set
	 */
	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}
	/**
	 * @return the mandateDate
	 */
	public Date getMandateDate() {
		return mandateDate;
	}
	/**
	 * @param mandateDate the mandateDate to set
	 */
	public void setMandateDate(Date mandateDate) {
		this.mandateDate = mandateDate;
	}
	/**
	 * @return the mandateIdentification
	 */
	public String getMandateIdentification() {
		return mandateIdentification;
	}
	/**
	 * @param mandateIdentification the mandateIdentification to set
	 */
	public void setMandateIdentification(String mandateIdentification) {
		this.mandateIdentification = mandateIdentification;
	}
	/**
	 * @return the info1
	 */
	public String getInfo1() {
		return info1;
	}
	/**
	 * @param info1 the info1 to set
	 */
	public void setInfo1(String info1) {
		this.info1 = info1;
	}
	/**
	 * @return the info2
	 */
	public String getInfo2() {
		return info2;
	}
	/**
	 * @param info2 the info2 to set
	 */
	public void setInfo2(String info2) {
		this.info2 = info2;
	}
	/**
	 * @return the info3
	 */
	public String getInfo3() {
		return info3;
	}
	/**
	 * @param info3 the info3 to set
	 */
	public void setInfo3(String info3) {
		this.info3 = info3;
	}
	/**
	 * @return the info4
	 */
	public String getInfo4() {
		return info4;
	}
	/**
	 * @param info4 the info4 to set
	 */
	public void setInfo4(String info4) {
		this.info4 = info4;
	}
	/**
	 * @return the info5
	 */
	public String getInfo5() {
		return info5;
	}
	/**
	 * @param info5 the info5 to set
	 */
	public void setInfo5(String info5) {
		this.info5 = info5;
	}

	public CustomFieldDto getCustomField() {
		return customField;
	}

	public void setCustomField(CustomFieldDto customField) {
		this.customField = customField;
	}

	
	
	
}
