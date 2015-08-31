package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.InvoiceCategoryDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetInvoiceCategoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceCategoryResponse extends BaseResponse {

	private static final long serialVersionUID = -8132109724455311508L;

	private InvoiceCategoryDto invoiceCategory;

	public InvoiceCategoryDto getInvoiceCategory() {
		return invoiceCategory;
	}

	public void setInvoiceCategory(InvoiceCategoryDto invoiceCategory) {
		this.invoiceCategory = invoiceCategory;
	}

	@Override
	public String toString() {
		return "GetInvoiceCategoryResponse [invoiceCategory=" + invoiceCategory + ", toString()=" + super.toString()
				+ "]";
	}
}
