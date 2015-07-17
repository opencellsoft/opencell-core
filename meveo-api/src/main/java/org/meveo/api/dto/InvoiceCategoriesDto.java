package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "InvoiceCategories")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceCategoriesDto implements Serializable {

	private static final long serialVersionUID = 7706480655782126035L;

	private List<InvoiceCategoryDto> invoiceCategory;

	public List<InvoiceCategoryDto> getInvoiceCategory() {
		if (invoiceCategory == null)
			invoiceCategory = new ArrayList<InvoiceCategoryDto>();
		return invoiceCategory;
	}

	public void setInvoiceCategory(List<InvoiceCategoryDto> invoiceCategory) {
		this.invoiceCategory = invoiceCategory;
	}

	@Override
	public String toString() {
		return "InvoiceCategoriesDto [invoiceCategory=" + invoiceCategory + "]";
	}

}
