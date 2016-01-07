package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "CustomerAccount")
@XmlType(name = "CustomerAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerAccountDto extends AccountDto {

	private static final long serialVersionUID = -137632696663739285L;

	@XmlElement(required = true)
	private String customer;

	@XmlElement(required = true)
	private String currency;

	@XmlElement(required = true)
	private String language;

	private String status;
	private String paymentMethod;
	private String creditCategory;
	private Date dateStatus;
	private Date dateDunningLevel;

	private ContactInformationDto contactInformation;

	private String dunningLevel;
	private String mandateIdentification = "";
	private Date mandateDate;
	private BigDecimal balance = BigDecimal.ZERO;
	// currently not use
	private Date terminationDate;

	/**
	 * Use for GET / LIST only.
	 */
	private BillingAccountsDto billingAccounts = new BillingAccountsDto();

	public CustomerAccountDto() {
		super();
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreditCategory() {
		return creditCategory;
	}

	public void setCreditCategory(String creditCategory) {
		this.creditCategory = creditCategory;
	}

	public Date getDateStatus() {
		return dateStatus;
	}

	public void setDateStatus(Date dateStatus) {
		this.dateStatus = dateStatus;
	}

	public Date getDateDunningLevel() {
		return dateDunningLevel;
	}

	public void setDateDunningLevel(Date dateDunningLevel) {
		this.dateDunningLevel = dateDunningLevel;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getDunningLevel() {
		return dunningLevel;
	}

	public void setDunningLevel(String dunningLevel) {
		this.dunningLevel = dunningLevel;
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

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "CustomerAccountDto [customer=" + customer + ", currency=" + currency + ", language=" + language + ", status=" + status + ", paymentMethod=" + paymentMethod
				+ ", creditCategory=" + creditCategory + ", dateStatus=" + dateStatus + ", dateDunningLevel=" + dateDunningLevel + ", contactInformation=" + contactInformation
				+ ", dunningLevel=" + dunningLevel + ", mandateIdentification=" + mandateIdentification + ", mandateDate=" + mandateDate + ", balance=" + balance
				+ ", terminationDate=" + terminationDate + ", billingAccounts=" + billingAccounts + "]";
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public BillingAccountsDto getBillingAccounts() {
		return billingAccounts;
	}

	public void setBillingAccounts(BillingAccountsDto billingAccounts) {
		this.billingAccounts = billingAccounts;
	}

	public ContactInformationDto getContactInformation() {
		if (contactInformation == null) {
			contactInformation = new ContactInformationDto();
		}
		return contactInformation;
	}

	public void setContactInformation(ContactInformationDto contactInformation) {
		this.contactInformation = contactInformation;
	}


}
