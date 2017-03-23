package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomersDto;

@XmlRootElement(name = "CustomerListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerListResponse extends BaseResponse {

	private static final long serialVersionUID = -7840902324622306237L;

	private CustomersDto customers;

	public CustomersDto getCustomers() {
		return customers;
	}

	public void setCustomers(CustomersDto customers) {
		this.customers = customers;
	}

	@Override
	public String toString() {
		return "CustomerListResponse [customers=" + customers + "]";
	}

}
