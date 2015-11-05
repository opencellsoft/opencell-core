package org.meveo.api.dto.account;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "Customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerDto extends AccountDto {

	private static final long serialVersionUID = 3243716253817571391L;

	@XmlElement(required = true)
	private String customerCategory;

	@XmlElement(required = true)
	private String customerBrand;

	@XmlElement(required = true)
	private String seller;

	private String mandateIdentification = "";
	private Date mandateDate;

	private ContactInformationDto contactInformation = new ContactInformationDto();
	
	/**
	 * Use for GET / LIST only.
	 */
	private CustomerAccountsDto customerAccounts = new CustomerAccountsDto();

	public CustomerDto() {
		super();
	}

	public CustomerDto(Customer e) {
		initFromEntity(e);
	}

	public void initFromEntity(Customer e) {
		super.initFromEntity(e);
		
		if (e.getCustomerCategory() != null) {
			customerCategory = e.getCustomerCategory().getCode();
		}

		if (e.getCustomerBrand() != null) {
			customerBrand = e.getCustomerBrand().getCode();
		}

		if (e.getSeller() != null) {
			seller = e.getSeller().getCode();
		}

		if (e.getContactInformation() != null) {
			contactInformation = new ContactInformationDto(e.getContactInformation());
		}

		if (!isLoaded() && e.getCustomerAccounts() != null) {
			customerAccounts = new CustomerAccountsDto();

			for (CustomerAccount ca : e.getCustomerAccounts()) {
				customerAccounts.getCustomerAccount().add(new CustomerAccountDto(ca));
			}
		}
		
		loaded = true;
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
