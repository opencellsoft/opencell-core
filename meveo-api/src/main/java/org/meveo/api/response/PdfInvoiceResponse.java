package org.meveo.api.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;


/**
 * @author R.AITYAAZZA
 *
 */
@XmlRootElement(name = "pdfInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class PdfInvoiceResponse {

	private ActionStatus actionStatus = new ActionStatus(
			ActionStatusEnum.SUCCESS, "");
	
	private byte[] pdfInvoice;

	public PdfInvoiceResponse() {

	}
 

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}


	public byte[] getPdfInvoice() {
		return pdfInvoice;
	}


	public void setPdfInvoice(byte[] pdfInvoice) {
		this.pdfInvoice = pdfInvoice;
	}


	

	

	

}
