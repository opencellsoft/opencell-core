package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.billing.PostInvoicingReportsDTO;

@XmlRootElement(name = "GetPostInvoicingReportsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPostInvoicingReportsResponseDto  extends BaseResponse{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PostInvoicingReportsDTO postInvoicingReportsDTO;
	
	public GetPostInvoicingReportsResponseDto(){
		
	}

	/**
	 * @return the PostInvoicingReportsDTO
	 */
	public PostInvoicingReportsDTO getPostInvoicingReportsDTO() {
		return postInvoicingReportsDTO;
	}

	/**
	 * @param PostInvoicingReportsDTO the PostInvoicingReportsDTO to set
	 */
	public void setPostInvoicingReportsDTO(PostInvoicingReportsDTO postInvoicingReportsDTO) {
		this.postInvoicingReportsDTO = postInvoicingReportsDTO;
	}


	@Override
	public String toString() {
		return "GetPostInvoicingReportsResponseDto [PostInvoicingReportsDTO=" + postInvoicingReportsDTO + "]";
	}
}
