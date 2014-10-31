package org.meveo.api.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.dto.CustomerAccountDto;


/**
 * @author R.AITYAAZZA
 *
 */
@XmlRootElement(name = "customerAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerAccountResponse {

	private ActionStatus actionStatus = new ActionStatus(
			ActionStatusEnum.SUCCESS, "");
	private CustomerAccountDto customerAccountDto;

	public CustomerAccountResponse() {

	}

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	public CustomerAccountDto getCustomerAccountDto() {
		return customerAccountDto;
	}

	public void setCustomerAccountDto(CustomerAccountDto customerAccountDto) {
		this.customerAccountDto = customerAccountDto;
	}



}
