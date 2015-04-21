package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "AccountOperationsDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountOperationsDto implements Serializable {

	private static final long serialVersionUID = -6969737909477126088L;

	private List<AccountOperationDto> accountOperation;

	public List<AccountOperationDto> getAccountOperation() {
		if (accountOperation == null) {
			accountOperation = new ArrayList<AccountOperationDto>();
		}
		return accountOperation;
	}

	public void setAccountOperation(List<AccountOperationDto> accountOperation) {
		this.accountOperation = accountOperation;
	}

	@Override
	public String toString() {
		return "AccountOperationsDto [accountOperation=" + accountOperation + "]";
	}
}
