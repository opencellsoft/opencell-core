package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.model.payments.CustomerAccount;

@XmlType(name = "CustomerAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerAccountDto extends AccountDto {

	private static final long serialVersionUID = -137632696663739285L;

	@XmlElement(required = true)
	private String customer;

	@XmlElement(required = true)
	private String currency;

	private String status;
	private String paymentMethod;
	private String creditCategory;
	private List<AccountOperationDto> accountOperations = new ArrayList<AccountOperationDto>();
	private Date dateStatus;
	private Date dateDunningLevel;

	private ContactInformationDto contactInformation = new ContactInformationDto();

	private String dunningLevel;
	private String mandateIdentification = "";
	private Date mandateDate;
	private BigDecimal balance = BigDecimal.ZERO;
	// currently not use
	private Date terminationDate;

	private BillingAccountsDto billingAccounts;

	public CustomerAccountDto() {
		super();
	}

	public CustomerAccountDto(CustomerAccount e) {
		super();

		if (e.getCustomer() != null) {
			customer = e.getCustomer().getCode();
		}

		if (e.getTradingCurrency() != null) {
			currency = e.getTradingCurrency().getCurrencyCode();
		}

		try {
			status = e.getStatus().name();
		} catch (NullPointerException ex) {
		}
		try {
			paymentMethod = e.getPaymentMethod().name();
		} catch (NullPointerException ex) {
		}
		try {
			creditCategory = e.getCreditCategory().name();
		} catch (NullPointerException ex) {
		}
		try {
			dunningLevel = e.getDunningLevel().name();
		} catch (NullPointerException ex) {
		}

		dateStatus = e.getDateStatus();
		dateDunningLevel = e.getDateDunningLevel();
		if (e.getContactInformation() != null) {
			contactInformation = new ContactInformationDto(e.getContactInformation());
		}

		mandateIdentification = e.getMandateIdentification();
		mandateDate = e.getMandateDate();
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

	public List<AccountOperationDto> getAccountOperations() {
		return accountOperations;
	}

	public void setAccountOperations(List<AccountOperationDto> accountOperations) {
		this.accountOperations = accountOperations;
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

	public void addAccountOperations(AccountOperationDto accountOperation) {
		if (accountOperations == null) {
			accountOperations = new ArrayList<AccountOperationDto>();
		}
		this.accountOperations.add(accountOperation);
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "CustomerAccountDto [customer=" + customer + ", currency=" + currency + ", status=" + status
				+ ", paymentMethod=" + paymentMethod + ", creditCategory=" + creditCategory + ", accountOperations="
				+ accountOperations + ", dateStatus=" + dateStatus + ", dateDunningLevel=" + dateDunningLevel
				+ ", contactInformation=" + contactInformation + ", dunningLevel=" + dunningLevel
				+ ", mandateIdentification=" + mandateIdentification + ", mandateDate=" + mandateDate + ", balance="
				+ balance + ", terminationDate=" + terminationDate + ", billingAccounts=" + billingAccounts + "]";
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
		return contactInformation;
	}

	public void setContactInformation(ContactInformationDto contactInformation) {
		this.contactInformation = contactInformation;
	}

}
