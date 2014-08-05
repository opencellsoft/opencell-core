package org.meveo.api.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.dto.CustomerHierarchyDto;

@XmlRootElement(name = "customerListsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerListResponse {

	private ActionStatus actionStatus = new ActionStatus(
			ActionStatusEnum.SUCCESS, "");
	private List<CustomerHierarchyDto> customerDtoList = new ArrayList<CustomerHierarchyDto>();

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	public List<CustomerHierarchyDto> getCustomerDtoList() {
		return customerDtoList;
	}

	public void setCustomerDtoList(List<CustomerHierarchyDto> customerDtoList) {
		this.customerDtoList = customerDtoList;
	}

}
