package org.meveo.api.dto.account;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.AccountEntity;
import org.meveo.model.crm.Customer;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Customer")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerDto extends AccountDto {

	private static final long serialVersionUID = 3243716253817571391L;

	@XmlElement(required = true)
	private String customerCategory;

	@XmlElement(required = true)
	private String customerBrand;

	@XmlElement(required = true)
	private String seller;

	private List<CustomerAccountDto> customerAccounts;

	public CustomerDto() {
		super();
	}

	public CustomerDto(Customer e) {
		super((AccountEntity) e);

		if (e.getCustomerCategory() != null) {
			customerCategory = e.getCustomerCategory().getCode();
		}

		if (e.getCustomerBrand() != null) {
			customerBrand = e.getCustomerBrand().getCode();
		}

		if (e.getSeller() != null) {
			seller = e.getSeller().getCode();
		}
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
		return "CustomerDto [customerCategory=" + customerCategory + ", customerBrand=" + customerBrand + ", seller="
				+ seller + ", customerAccounts=" + customerAccounts + "]";
	}

	public List<CustomerAccountDto> getCustomerAccounts() {
		return customerAccounts;
	}

	public void setCustomerAccounts(List<CustomerAccountDto> customerAccounts) {
		this.customerAccounts = customerAccounts;
	}

}
