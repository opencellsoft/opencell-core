package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomerHierarchyDto;

@XmlRootElement(name = "CustomerListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerListResponse extends BaseResponse {

	private static final long serialVersionUID = -7840902324622306237L;

	private List<CustomerHierarchyDto> customerDtoList = new ArrayList<CustomerHierarchyDto>();
	
	public CustomerListResponse() {
		super();
	}

	public List<CustomerHierarchyDto> getCustomerDtoList() {
		return customerDtoList;
	}

	public void setCustomerDtoList(List<CustomerHierarchyDto> customerDtoList) {
		this.customerDtoList = customerDtoList;
	}

}
