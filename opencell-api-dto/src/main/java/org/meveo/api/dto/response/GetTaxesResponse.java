package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.TaxesDto;

/**
 * The Class GetTaxesResponse.
 */
@XmlRootElement(name = "GetTaxesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTaxesResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The taxes dto. */
    private TaxesDto taxesDto;

    /**
     * Gets the taxes dto.
     *
     * @return the taxes dto
     */
    public TaxesDto getTaxesDto() {
        return taxesDto;
    }

    /**
     * Sets the taxes dto.
     *
     * @param taxesDto the new taxes dto
     */
    public void setTaxesDto(TaxesDto taxesDto) {
        this.taxesDto = taxesDto;
    }

    @Override
    public String toString() {
        return "GetTaxesResponse [taxesDto=" + taxesDto + "]";
    }
}