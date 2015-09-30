package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.CustomersDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetAccountHierarchyResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAccountHierarchyResponseDto extends BaseResponse {

	private static final long serialVersionUID = 8676287369018121754L;

	private CustomersDto customers;

	public CustomersDto getCustomers() {
		if (customers == null) {
			customers = new CustomersDto();
		}
		return customers;
	}

	public void setCustomers(CustomersDto customers) {
		this.customers = customers;
	}

}
