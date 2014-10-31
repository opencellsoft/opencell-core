package org.meveo.api.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.dto.PaymentDto;


@XmlRootElement(name = "customerPaymentsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerPaymentsResponse {

	private ActionStatus actionStatus = new ActionStatus(
			ActionStatusEnum.SUCCESS, "");
	private List<PaymentDto> customerPaymentDtoList;
	private double balance;

	public CustomerPaymentsResponse() {

	}

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	public List<PaymentDto> getCustomerPaymentDtoList() {
		return customerPaymentDtoList;
	}

	public void setCustomerPaymentDtoList(
			List<PaymentDto> customerPaymentDtoList) {
		this.customerPaymentDtoList = customerPaymentDtoList;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	

}
