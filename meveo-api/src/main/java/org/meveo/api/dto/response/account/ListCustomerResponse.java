package org.meveo.api.dto.response.account;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListCustomerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListCustomerResponse extends BaseResponse {

	private static final long serialVersionUID = -7212880976584184812L;

	private List<CustomerDto> customers;

	public List<CustomerDto> getCustomers() {
		return customers;
	}

	public void setCustomers(List<CustomerDto> customers) {
		this.customers = customers;
	}

}
