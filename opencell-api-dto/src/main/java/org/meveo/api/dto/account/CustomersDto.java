package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.meveo.model.crm.Customer;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "Customers")
@XmlAccessorType(XmlAccessType.FIELD)
@FilterResults(property = "customer", entityClass = Customer.class)
public class CustomersDto implements Serializable {

	private static final long serialVersionUID = -1693325835765290126L;

	private List<CustomerDto> customer;

	public List<CustomerDto> getCustomer() {
		if (customer == null) {
			customer = new ArrayList<CustomerDto>();
		}

		return customer;
	}

	public void setCustomer(List<CustomerDto> customer) {
		this.customer = customer;
	}

	@Override
	public String toString() {
		return "CustomersDto [customer=" + customer + "]";
	}

}
