package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.BillingRunDto;
import org.meveo.api.dto.response.BaseResponse;


@XmlRootElement(name = "GetBillingRunInfoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBillingRunInfoResponseDto extends BaseResponse{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BillingRunDto billingRunDto;
	
	public GetBillingRunInfoResponseDto(){
		
	}

	/**
	 * @return the billingRunDto
	 */
	public BillingRunDto getBillingRunDto() {
		return billingRunDto;
	}

	/**
	 * @param billingRunDto the billingRunDto to set
	 */
	public void setBillingRunDto(BillingRunDto billingRunDto) {
		this.billingRunDto = billingRunDto;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GetBillingRunInfoResponse [billingRunDto=" + billingRunDto + "]";
	}

}
