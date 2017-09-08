package org.meveo.api.dto.account;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.CustomerAccount;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name="Customer")
@XmlAccessorType(XmlAccessType.FIELD)
@FilterResults(property = "customerAccounts.customerAccount", entityClass = CustomerAccount.class)
public class CustomerDto extends AccountDto {

	private static final long serialVersionUID = 3243716253817571391L;

	@XmlElement(required = true)
	private String customerCategory;

	@XmlElement()
	private String customerBrand;

	@XmlElement(required = true)
	private String seller;

	private String mandateIdentification = "";
	private Date mandateDate;

	private ContactInformationDto contactInformation;

	/**
	 * Use for GET / LIST only.
	 */
	private CustomerAccountsDto customerAccounts = new CustomerAccountsDto();

	public CustomerDto() {
		super();
	}

	public String getCustomerCategory() {
		return customerCategory;
	}

	public void setCustomerCategory(String customerCategory) {
		this.customerCategory = customerCategory;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getCustomerBrand() {
		return customerBrand;
	}

	public void setCustomerBrand(String customerBrand) {
		this.customerBrand = customerBrand;
	}

	@Override
	public String toString() {
		return "CustomerDto [customerCategory=" + customerCategory + ", customerBrand=" + customerBrand + ", seller=" + seller + ", mandateIdentification=" + mandateIdentification
				+ ", mandateDate=" + mandateDate + ", contactInformation=" + contactInformation + ", customerAccounts=" + customerAccounts + "]";
	}

	public CustomerAccountsDto getCustomerAccounts() {
		return customerAccounts;
	}

	public void setCustomerAccounts(CustomerAccountsDto customerAccounts) {
		this.customerAccounts = customerAccounts;
	}

	public ContactInformationDto getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(ContactInformationDto contactInformation) {
		this.contactInformation = contactInformation;
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

}
