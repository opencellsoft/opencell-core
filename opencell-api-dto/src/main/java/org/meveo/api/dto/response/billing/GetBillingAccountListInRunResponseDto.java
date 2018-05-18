package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetBillingAccountListInRunResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetBillingAccountListInRunResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBillingAccountListInRunResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The billing accounts dto. */
    private BillingAccountsDto billingAccountsDto;

    /**
     * Instantiates a new gets the billing account list in run response dto.
     */
    public GetBillingAccountListInRunResponseDto() {

    }

    /**
     * Gets the billing accounts dto.
     *
     * @return the billingAccountsDto
     */
    public BillingAccountsDto getBillingAccountsDto() {
        return billingAccountsDto;
    }

    /**
     * Sets the billing accounts dto.
     *
     * @param billingAccountsDto the billingAccountsDto to set
     */
    public void setBillingAccountsDto(BillingAccountsDto billingAccountsDto) {
        this.billingAccountsDto = billingAccountsDto;
    }

    @Override
    public String toString() {
        return "GetBillingAccountListInRunResponseDto [billingAccountsDto=" + billingAccountsDto + "]";
    }
}