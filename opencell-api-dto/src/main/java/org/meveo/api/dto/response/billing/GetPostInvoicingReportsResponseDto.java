package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.billing.PostInvoicingReportsDTO;

/**
 * The Class GetPostInvoicingReportsResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetPostInvoicingReportsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPostInvoicingReportsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The post invoicing reports DTO. */
    private PostInvoicingReportsDTO postInvoicingReportsDTO;

    /**
     * Instantiates a new gets the post invoicing reports response dto.
     */
    public GetPostInvoicingReportsResponseDto() {

    }

    /**
     * Gets the post invoicing reports DTO.
     *
     * @return the PostInvoicingReportsDTO
     */
    public PostInvoicingReportsDTO getPostInvoicingReportsDTO() {
        return postInvoicingReportsDTO;
    }

    /**
     * Sets the post invoicing reports DTO.
     *
     * @param postInvoicingReportsDTO the PostInvoicingReportsDTO to set
     */
    public void setPostInvoicingReportsDTO(PostInvoicingReportsDTO postInvoicingReportsDTO) {
        this.postInvoicingReportsDTO = postInvoicingReportsDTO;
    }

    @Override
    public String toString() {
        return "GetPostInvoicingReportsResponseDto [PostInvoicingReportsDTO=" + postInvoicingReportsDTO + "]";
    }
}