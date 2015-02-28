package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListBillingAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListBillingAccountResponseDto extends BaseResponse {

	private static final long serialVersionUID = 583740580596077812L;

	private BillingAccountsDto billingAccounts = new BillingAccountsDto();

	public BillingAccountsDto getBillingAccounts() {
		return billingAccounts;
	}

	public void setBillingAccounts(BillingAccountsDto billingAccounts) {
		this.billingAccounts = billingAccounts;
	}

	@Override
	public String toString() {
		return "ListBillingAccountResponseDto [billingAccounts=" + billingAccounts + ", toString()=" + super.toString()
				+ "]";
	}

}
