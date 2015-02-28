package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetCustomerAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBillingAccountResponse extends BaseResponse {

	private static final long serialVersionUID = -8538364402251002467L;

	private BillingAccountDto billingAccount;

	public BillingAccountDto getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(BillingAccountDto billingAccount) {
		this.billingAccount = billingAccount;
	}

	@Override
	public String toString() {
		return "GetBillingAccountResponse [billingAccount=" + billingAccount + ", toString()=" + super.toString() + "]";
	}

}
