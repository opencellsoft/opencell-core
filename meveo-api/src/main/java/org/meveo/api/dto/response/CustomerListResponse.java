package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.AccountHierarchyDto;

@XmlRootElement(name = "CustomerListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerListResponse extends BaseResponse {

	private static final long serialVersionUID = -7840902324622306237L;

	private List<AccountHierarchyDto> customerDtoList = new ArrayList<AccountHierarchyDto>();
	
	public CustomerListResponse() {
		super();
	}

	public List<AccountHierarchyDto> getCustomerDtoList() {
		return customerDtoList;
	}

	public void setCustomerDtoList(List<AccountHierarchyDto> customerDtoList) {
		this.customerDtoList = customerDtoList;
	}

	@Override
	public String toString() {
		return "CustomerListResponse [customerDtoList=" + customerDtoList + ", toString()=" + super.toString() + "]";
	}

}
