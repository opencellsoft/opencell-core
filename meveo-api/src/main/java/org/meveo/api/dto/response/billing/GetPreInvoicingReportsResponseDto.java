package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.billing.PreInvoicingReportsDTO;

@XmlRootElement(name = "GetPreInvoicingReportsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPreInvoicingReportsResponseDto  extends BaseResponse{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PreInvoicingReportsDTO preInvoicingReportsDTO;
	
	public GetPreInvoicingReportsResponseDto(){
		
	}

	/**
	 * @return the preInvoicingReportsDTO
	 */
	public PreInvoicingReportsDTO getPreInvoicingReportsDTO() {
		return preInvoicingReportsDTO;
	}

	/**
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
