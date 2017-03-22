package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "LitigationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class LitigationRequestDto {

	private String customerAccountCode;
	private Long accountOperationId;
	
	public String getCustomerAccountCode() {
		return customerAccountCode;
	}
	public void setCustomerAccountCode(String customerAccountCode) {
		this.customerAccountCode = customerAccountCode;
	}
	public Long getAccountOperationId() {
		return accountOperationId;
	}
	public void setAccountOperationId(Long accountOperationId) {
		this.accountOperationId = accountOperationId;
	}
	
	
	


}