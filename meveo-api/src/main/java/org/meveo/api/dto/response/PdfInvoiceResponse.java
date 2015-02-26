package org.meveo.api.dto.response;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author R.AITYAAZZA
 *
 */
@XmlRootElement(name = "PdfInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PdfInvoiceResponse extends BaseResponse {

	private static final long serialVersionUID = 3909724929599303450L;

	private byte[] pdfInvoice;

	public PdfInvoiceResponse() {
		super();
	}

	public byte[] getPdfInvoice() {
		return pdfInvoice;
	}

	public void setPdfInvoice(byte[] pdfInvoice) {
		this.pdfInvoice = pdfInvoice;
	}

	@Override
	public String toString() {
		return "PdfInvoiceResponse [pdfInvoice=" + Arrays.toString(pdfInvoice) + ", toString()=" + super.toString()
				+ "]";
	}

}
