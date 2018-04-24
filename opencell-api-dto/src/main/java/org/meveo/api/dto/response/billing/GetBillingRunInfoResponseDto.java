package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.BillingRunDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetBillingRunInfoResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetBillingRunInfoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBillingRunInfoResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The billing run dto. */
    private BillingRunDto billingRunDto;

    /**
     * Instantiates a new gets the billing run info response dto.
     */
    public GetBillingRunInfoResponseDto() {

    }

    /**
     * Gets the billing run dto.
     *
     * @return the billingRunDto
     */
    public BillingRunDto getBillingRunDto() {
        return billingRunDto;
    }

    /**
     * Sets the billing run dto.
     *
     * @param billingRunDto the billingRunDto to set
     */
    public void setBillingRunDto(BillingRunDto billingRunDto) {
        this.billingRunDto = billingRunDto;
    }

    @Override
    public String toString() {
        return "GetBillingRunInfoResponse [billingRunDto=" + billingRunDto + "]";
    }
}