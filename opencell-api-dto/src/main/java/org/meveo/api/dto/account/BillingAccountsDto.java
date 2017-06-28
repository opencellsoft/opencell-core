package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Edward P. Legaspi
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingAccountsDto implements Serializable {

	private static final long serialVersionUID = -2034319035301583131L;

	private List<BillingAccountDto> billingAccount;

	public List<BillingAccountDto> getBillingAccount() {
		if (billingAccount == null) {
			billingAccount = new ArrayList<BillingAccountDto>();
		}

		return billingAccount;
	}

	public void setBillingAccount(List<BillingAccountDto> billingAccount) {
		this.billingAccount = billingAccount;
	}

	@Override
	public String toString() {
		return "BillingAccountsDto [billingAccount=" + billingAccount + "]";
	}

}
