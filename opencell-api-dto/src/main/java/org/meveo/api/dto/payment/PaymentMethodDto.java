package org.meveo.api.dto.payment;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.api.message.exception.InvalidDTOException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CheckPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.TipPaymentMethod;
import org.meveo.model.payments.WirePaymentMethod;

@XmlRootElement(name = "PaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentMethodDto extends BaseDto {

	private static final long serialVersionUID = 4815935377652350103L;

	/**
	 * PaymentMethod type
	 */
	@NotNull
	@XmlAttribute()
	private PaymentMethodEnum paymentMethodType;

	private Long id;

	private String alias;

	private boolean preferred;

	private String customerAccountCode;

	private String info1;

	private String info2;

	private String info3;

	private String info4;

	private String info5;

	/**
	 * Bank account information
	 */
	private BankCoordinatesDto bankCoordinates;   
	private String mandateIdentification ;
	private Date mandateDate;

	/**
	 * Card type
	 */
	private CreditCardTypeEnum cardType;

	/**
	 * Cardholder: first and last name
	 */
	private String owner;

	/**
	 * Card expiration: month
	 */
	private Integer monthExpiration;

	/**
	 * Card expiration: year
	 */
	private Integer yearExpiration;

	/**
	 * Token ID in a payment gateway
	 */
	private String tokenId;

	/**
	 * Card number: full number , with first 12 digits hiding in read operation
	 */
	private String cardNumber;

	/**
	 * Issue number
	 */
	private String issueNumber;

	/**
	 * User identifier
	 */
	private String userId;



	public PaymentMethodDto() {
	}

	public PaymentMethodDto(PaymentMethodEnum paymentType) {
		this.paymentMethodType = paymentType;
		this.alias = "default";
		this.preferred = true;    	
	}

	public PaymentMethodDto(PaymentMethodEnum paymentType,BankCoordinatesDto bankCoordinatesDto, String mandateIdentification, Date mandateDate ) {
		this(paymentType);    
		this.bankCoordinates = bankCoordinatesDto;
		this.mandateIdentification = mandateIdentification;
		this.mandateDate = mandateDate;
		validate();
	}

	public PaymentMethodDto(PaymentMethod paymentMethod) {
		this.id = paymentMethod.getId();
		this.alias = paymentMethod.getAlias();
		this.preferred = paymentMethod.isPreferred();		
		this.userId = paymentMethod.getUserId();
		this.info1 = paymentMethod.getInfo1();
		this.info2 = paymentMethod.getInfo2();
		this.info3 = paymentMethod.getInfo3();
		this.info4 = paymentMethod.getInfo4();
		this.info5 = paymentMethod.getInfo5();	
		if(paymentMethod.getCustomerAccount() != null){
			this.customerAccountCode = paymentMethod.getCustomerAccount().getCode();
		}
		if(paymentMethod instanceof DDPaymentMethod){
			this.setPaymentMethodType(PaymentMethodEnum.DIRECTDEBIT);
			this.mandateDate = ((DDPaymentMethod) paymentMethod).getMandateDate();
			this.mandateIdentification = ((DDPaymentMethod) paymentMethod).getMandateIdentification();
			this.bankCoordinates = new BankCoordinatesDto(((DDPaymentMethod) paymentMethod).getBankCoordinates());
		}
		if(paymentMethod instanceof TipPaymentMethod){        	
			this.setPaymentMethodType(PaymentMethodEnum.TIP);
			this.bankCoordinates = new BankCoordinatesDto(((TipPaymentMethod)paymentMethod).getBankCoordinates());
		}
		if(paymentMethod instanceof CardPaymentMethod){			
			this.setPaymentMethodType(PaymentMethodEnum.CARD);        	
			this.cardNumber = ((CardPaymentMethod)paymentMethod).getHiddenCardNumber();
			this.owner = ((CardPaymentMethod)paymentMethod).getOwner();
			this.cardType = ((CardPaymentMethod)paymentMethod).getCardType();
			this.monthExpiration = ((CardPaymentMethod)paymentMethod).getMonthExpiration();
			this.yearExpiration = ((CardPaymentMethod)paymentMethod).getYearExpiration();
			this.issueNumber = ((CardPaymentMethod)paymentMethod).getIssueNumber();
			this.tokenId = ((CardPaymentMethod)paymentMethod).getTokenId();
		}
		if(paymentMethod instanceof CheckPaymentMethod){
			this.setPaymentMethodType(PaymentMethodEnum.CHECK);           
		}
		if(paymentMethod instanceof WirePaymentMethod){
			this.setPaymentMethodType(PaymentMethodEnum.WIRETRANSFER);           
		} 
	}

	public PaymentMethodDto(CardPaymentMethodDto cardPaymentMethodDto) {
		this.setPaymentMethodType(PaymentMethodEnum.CARD);
		this.id = cardPaymentMethodDto.getId();
		this.alias = cardPaymentMethodDto.getAlias();
		this.preferred = cardPaymentMethodDto.isPreferred();
		this.userId = cardPaymentMethodDto.getUserId();
		this.info1 = cardPaymentMethodDto.getInfo1();
		this.info2 = cardPaymentMethodDto.getInfo2();
		this.info3 = cardPaymentMethodDto.getInfo3();
		this.info4 = cardPaymentMethodDto.getInfo4();
		this.info5 = cardPaymentMethodDto.getInfo5();
		this.customerAccountCode = cardPaymentMethodDto.getCustomerAccountCode();
		this.cardNumber = cardPaymentMethodDto.getCardNumber();
		this.owner = cardPaymentMethodDto.getOwner();
		this.cardType = cardPaymentMethodDto.getCardType();
		this.monthExpiration = cardPaymentMethodDto.getMonthExpiration();
		this.yearExpiration = cardPaymentMethodDto.getYearExpiration();
		this.issueNumber = cardPaymentMethodDto.getIssueNumber();
		this.tokenId = cardPaymentMethodDto.getTokenId();
		validate();
	}

	public PaymentMethod fromDto(CustomerAccount customerAccount) {
		PaymentMethod pmEntity = null;
		switch (getPaymentMethodType()) {
		case CARD:
			pmEntity = new CardPaymentMethod(customerAccount,getAlias(),getCardNumber(),getOwner(),isPreferred(),getIssueNumber(),getYearExpiration(),getMonthExpiration(),getCardType());
			break;

		case DIRECTDEBIT:
			pmEntity = new DDPaymentMethod(customerAccount,getAlias(),getMandateDate(),getMandateIdentification(),getBankCoordinates() != null ? getBankCoordinates().fromDto() : null);
			break;

		case TIP:
			pmEntity = new TipPaymentMethod(customerAccount,getAlias(),getBankCoordinates() != null ? getBankCoordinates().fromDto() : null);
			break;

		case CHECK:
			pmEntity = new CheckPaymentMethod(alias, preferred, customerAccount);
			break;

		case WIRETRANSFER:
			pmEntity = new WirePaymentMethod(alias, preferred, customerAccount);
			break;
		}
		return pmEntity;
	}

	public PaymentMethod updateFromDto(PaymentMethod paymentMethod) {
		if (isPreferred()) {
			paymentMethod.setPreferred(true);
		}else{
			paymentMethod.setPreferred(false);
		}

		if (!StringUtils.isBlank(getAlias())) {
			paymentMethod.setAlias(getAlias());
		}
		switch (getPaymentMethodType()) {
	
		case DIRECTDEBIT:

			if (!StringUtils.isBlank(getMandateIdentification())) {
				((DDPaymentMethod)paymentMethod).setMandateIdentification(getMandateIdentification());
			}

			if (isPreferred()) {
				((DDPaymentMethod)paymentMethod).setPreferred(true);
			}

			if (!StringUtils.isBlank(getAlias())) {
				((DDPaymentMethod)paymentMethod).setAlias(getAlias());
			}
			if (!StringUtils.isBlank(getMandateIdentification())) {
				((DDPaymentMethod)paymentMethod).setMandateIdentification(getMandateIdentification());
			}
			if (!StringUtils.isBlank(getMandateDate())) {
				((DDPaymentMethod)paymentMethod).setMandateDate(getMandateDate());
			}
			if(getBankCoordinates() != null){
				if (!StringUtils.isBlank(getBankCoordinates().getAccountNumber())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getAccountNumber());
				}
				if (!StringUtils.isBlank(getBankCoordinates().getAccountOwner())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getAccountOwner());
				}	
				if (!StringUtils.isBlank(getBankCoordinates().getBankCode())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getBankCode());
				}
				if (!StringUtils.isBlank(getBankCoordinates().getBankId())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getBankId());
				}
				if (!StringUtils.isBlank(getBankCoordinates().getBankName())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getBankName());
				}
				if (!StringUtils.isBlank(getBankCoordinates().getBic())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getBic());
				}			
				if (!StringUtils.isBlank(getBankCoordinates().getBranchCode())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getBranchCode());
				}	
				if (!StringUtils.isBlank(getBankCoordinates().getIban())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getIban());
				}	
				if (!StringUtils.isBlank(getBankCoordinates().getIcs())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getIcs());
				}	
				if (!StringUtils.isBlank(getBankCoordinates().getIssuerName())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getIssuerName());
				}	
				if (!StringUtils.isBlank(getBankCoordinates().getIssuerNumber())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getIssuerNumber());
				}
				if (!StringUtils.isBlank(getBankCoordinates().getKey())) {
					((DDPaymentMethod)paymentMethod).getBankCoordinates().setAccountNumber(getBankCoordinates().getKey());
				}		
			}
			break;
		}
		return paymentMethod;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean isPreferred() {
		return preferred;
	}

	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}

	public String getCustomerAccountCode() {
		return customerAccountCode;
	}

	public void setCustomerAccountCode(String customerAccountCode) {
		this.customerAccountCode = customerAccountCode;
	}



	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	public String getInfo4() {
		return info4;
	}

	public void setInfo4(String info4) {
		this.info4 = info4;
	}

	public String getInfo5() {
		return info5;
	}

	public void setInfo5(String info5) {
		this.info5 = info5;
	}



	public PaymentMethodEnum getPaymentMethodType() {
		return paymentMethodType;
	}

	public void setPaymentMethodType(PaymentMethodEnum paymentType) {
		this.paymentMethodType = paymentType;
	}

	public BankCoordinatesDto getBankCoordinates() {
		return bankCoordinates;
	}

	public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
		this.bankCoordinates = bankCoordinates;
	}

	public String getMandateIdentification() {
		return mandateIdentification;
	}

	public void setMandateIdentification(String mandateIdentification) {
		this.mandateIdentification = mandateIdentification;
	}

	public Date getMandateDate() {
		return mandateDate;
	}

	public void setMandateDate(Date mandateDate) {
		this.mandateDate = mandateDate;
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

	public void validate() {
		validate(false); 
	}
	public void validate(boolean isRoot) {
		if(getPaymentMethodType() == null){
			throw new InvalidDTOException("Missing payment method type");
		}
		if (isRoot && StringUtils.isBlank(getCustomerAccountCode())) {	           
			throw new InvalidDTOException("Missing customerAccountCode");
		}
		if(getPaymentMethodType() == PaymentMethodEnum.CARD){
			if(StringUtils.isBlank(getCardNumber()) || getCardNumber().length() != 16){
				throw new InvalidDTOException("Invalid cardNumber");	
			}		        
			if (StringUtils.isBlank(getOwner())) {
				throw new InvalidDTOException("Missing Owner");
			}
			if (StringUtils.isBlank(getMonthExpiration()) || StringUtils.isBlank(getYearExpiration())) {
				throw new InvalidDTOException("Missing expiryDate");
			} 
			return;
		}
		if(getPaymentMethodType() == PaymentMethodEnum.DIRECTDEBIT){
			if (getBankCoordinates() == null && StringUtils.isBlank(getMandateIdentification())) {
				throw new InvalidDTOException("Missing Bank coordinates or MandateIdentification");
			}
			if (getBankCoordinates() != null) {
				validateBankCoordinates(getBankCoordinates());
			}else{
				if(StringUtils.isBlank(getMandateIdentification())){
					throw new InvalidDTOException("Missing MandateIdentification");	
				}
				if(StringUtils.isBlank(getMandateDate())){
					throw new InvalidDTOException("Missing MandateDate");	
				}
			}
			return;
		}
		if(getPaymentMethodType() == PaymentMethodEnum.TIP){
			validateBankCoordinates(getBankCoordinates());
			return;
		}

	}

	private void validateBankCoordinates(BankCoordinatesDto bankCoordinatesDto){
		if (getBankCoordinates() == null ) {
			throw new InvalidDTOException("Missing Bank coordinates");
		}
		if(StringUtils.isBlank(getBankCoordinates().getAccountNumber())){
			throw new InvalidDTOException("Missing AccountNumber");	
		}
		if(StringUtils.isBlank(getBankCoordinates().getAccountOwner())){
			throw new InvalidDTOException("Missing AccountOwner");	
		}
		if(StringUtils.isBlank(getBankCoordinates().getBankCode())){
			throw new InvalidDTOException("Missing BankCode");	
		}   
		if(StringUtils.isBlank(getBankCoordinates().getBankName())){
			throw new InvalidDTOException("Missing BankName");	
		}  
		if(StringUtils.isBlank(getBankCoordinates().getIban())){
			throw new InvalidDTOException("Missing Iban");	
		}   
	}

	@Override
	public String toString() {
		return "PaymentMethodDto [paymentMethodType=" + paymentMethodType + ", id=" + id + ", alias=" + alias
				+ ", preferred=" + preferred + ", customerAccountCode=" + customerAccountCode + ", info1=" + info1
				+ ", info2=" + info2 + ", info3=" + info3 + ", info4=" + info4 + ", info5=" + info5
				+ ", bankCoordinates=" + bankCoordinates + ", mandateIdentification=" + mandateIdentification
				+ ", mandateDate=" + mandateDate + ", cardType=" + cardType + ", owner=" + owner + ", monthExpiration="
				+ monthExpiration + ", yearExpiration=" + yearExpiration + ", tokenId=" + tokenId + ", cardNumber="
				+ StringUtils.hideCardNumber(cardNumber) + ", issueNumber=" + issueNumber + ", userId=" + userId + "]";
	}

}