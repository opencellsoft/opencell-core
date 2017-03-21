package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetBillingAccountListInRunResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBillingAccountListInRunResponseDto  extends BaseResponse{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BillingAccountsDto billingAccountsDto;
	
	public GetBillingAccountListInRunResponseDto(){
		
	}

	/**
	 * @return the billingAccountsDto
	 */
	public BillingAccountsDto getBillingAccountsDto() {
		return billingAccountsDto;
	}

	/**
	 * @param billingAccountsDto the billingAccountsDto to set
	 */
	public void setBillingAccountsDto(BillingAccountsDto billingAccountsDto) {
		this.billingAccountsDto = billingAccountsDto;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GetBillingAccountListInRunResponseDto [billingAccountsDto=" + billingAccountsDto + "]";
	}

}
