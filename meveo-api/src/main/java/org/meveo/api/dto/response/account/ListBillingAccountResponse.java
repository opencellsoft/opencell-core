package org.meveo.api.dto.response.account;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BillingAccountDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListBillingAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListBillingAccountResponse extends BaseResponse {

	private static final long serialVersionUID = 583740580596077812L;

	private List<BillingAccountDto> billingAccounts;

	public List<BillingAccountDto> getBillingAccounts() {
		return billingAccounts;
	}

	public void setBillingAccounts(List<BillingAccountDto> billingAccounts) {
		this.billingAccounts = billingAccounts;
	}

}
