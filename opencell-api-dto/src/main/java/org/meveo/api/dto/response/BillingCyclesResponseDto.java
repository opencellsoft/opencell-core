package org.meveo.api.dto.response;


import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.BillingCyclesDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class BillingCyclesResponseDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "BillingCyclesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingCyclesResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6134470575443721802L;

    /** The billingCycles DTO. */
    private BillingCyclesDto billingCyclesDto = new BillingCyclesDto();

    /**
     * Constructor of BillingCycleResponseDto.
     */
    public BillingCyclesResponseDto() {
    }

    public BillingCyclesResponseDto(GenericSearchResponse<BillingCycleDto> searchResponse) {
        super(searchResponse.getPaging());
        this.billingCyclesDto.setBillingCycle(searchResponse.getSearchResults());
    }

    /**
     * Get the billingCycles DTO.
     *
     * @return the billingCycles DTO
     */
    public BillingCyclesDto getBillingCycles() {
        return billingCyclesDto;
    }

    /**
     * Sets the billingCycles DTO.
     *
     * @param billingCyclesDto the billingCycles DTO
     */
    public void setBillingCycles(BillingCyclesDto billingCyclesDto) {
        this.billingCyclesDto = billingCyclesDto;
    }

    @Override
    public String toString() {
        return "ListBillingCycleResponseDto [billingCycles=" + billingCyclesDto + ", toString()=" + super.toString() + "]";
    }
}
