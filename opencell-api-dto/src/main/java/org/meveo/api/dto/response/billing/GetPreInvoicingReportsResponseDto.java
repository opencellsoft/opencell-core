package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.billing.PreInvoicingReportsDTO;

/**
 * The Class GetPreInvoicingReportsResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetPreInvoicingReportsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPreInvoicingReportsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The pre invoicing reports DTO. */
    private PreInvoicingReportsDTO preInvoicingReportsDTO;

    /**
     * Instantiates a new gets the pre invoicing reports response dto.
     */
    public GetPreInvoicingReportsResponseDto() {

    }

    /**
     * Gets the pre invoicing reports DTO.
     *
     * @return the preInvoicingReportsDTO
     */
    public PreInvoicingReportsDTO getPreInvoicingReportsDTO() {
        return preInvoicingReportsDTO;
    }

    /**
     * Sets the pre invoicing reports DTO.
     *
     * @param preInvoicingReportsDTO the preInvoicingReportsDTO to set
     */
    public void setPreInvoicingReportsDTO(PreInvoicingReportsDTO preInvoicingReportsDTO) {
        this.preInvoicingReportsDTO = preInvoicingReportsDTO;
    }

    @Override
    public String toString() {
        return "GetPreInvoicingReportsResponseDto [preInvoicingReportsDTO=" + preInvoicingReportsDTO + "]";
    }
}