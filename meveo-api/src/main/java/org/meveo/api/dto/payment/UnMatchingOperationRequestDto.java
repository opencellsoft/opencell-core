package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "UnMatchingOperationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnMatchingOperationRequestDto {

	private String customerAccount;
	private Long accountOperationId;
	
	public String getCustomerAccount() {
		return customerAccount;
	}
	public void setCustomerAccount(String customerAccount) {
		this.customerAccount = customerAccount;
	}
	public Long getAccountOperationId() {
		return accountOperationId;
	}
	public void setAccountOperationId(Long accountOperationId) {
		this.accountOperationId = accountOperationId;
	}
	
	
	


}