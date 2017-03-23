package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.PaymentDto;

@XmlRootElement(name = "CustomerPaymentsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerPaymentsResponse extends BaseResponse {

	private static final long serialVersionUID = -5831455659437348223L;

	private List<PaymentDto> customerPaymentDtoList;
	private double balance;

	public CustomerPaymentsResponse() {
		super();
	}

	public List<PaymentDto> getCustomerPaymentDtoList() {
		return customerPaymentDtoList;
	}

	public void setCustomerPaymentDtoList(List<PaymentDto> customerPaymentDtoList) {
		this.customerPaymentDtoList = customerPaymentDtoList;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "CustomerPaymentsResponse [customerPaymentDtoList=" + customerPaymentDtoList + ", balance=" + balance
				+ ", toString()=" + super.toString() + "]";
	}

}
