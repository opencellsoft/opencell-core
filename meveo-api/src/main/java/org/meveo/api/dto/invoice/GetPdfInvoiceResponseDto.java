package org.meveo.api.dto.invoice;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;


@XmlRootElement(name = "GetPdfInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPdfInvoiceResponseDto extends BaseResponse{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] pdfContent;
	
	public GetPdfInvoiceResponseDto(){
		
	}

	/**
	 * @return the pdfContent
	 */
	public byte[] getPdfContent() {
		return pdfContent;
	}

	/**
	 * @param pdfContent the pdfContent to set
	 */
	public void setPdfContent(byte[] pdfContent) {
		this.pdfContent = pdfContent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GetPdfInvoiceResponseDto [pdfContent=" + Arrays.toString(pdfContent) + "]";
	}

		
}
