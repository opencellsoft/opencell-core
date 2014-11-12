package org.meveo.api.dto.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AccountOperationDto;
import org.meveo.api.dto.BaseDto;

@XmlRootElement(name = "CustomerAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerAccountDto extends BaseDto {

	private static final long serialVersionUID = -137632696663739285L;

	private String status;
	private String paymentMethod;
	private String creditCategory;
	private List<AccountOperationDto> accountOperations = new ArrayList<AccountOperationDto>();
	private Date dateStatus;
	private Date dateDunningLevel;

	private String email;
	private String phone;
	private String mobile;
	private String fax;

	private String customerCode;
	private String dunningLevel;
	private String mandateIdentification = "";
	private Date mandateDate;
	private BigDecimal balance = BigDecimal.ZERO;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
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

}
