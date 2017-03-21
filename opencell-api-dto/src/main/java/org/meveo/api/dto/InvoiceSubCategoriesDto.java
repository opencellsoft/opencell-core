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
@XmlRootElement(name = "InvoiceSubCategories")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSubCategoriesDto implements Serializable {

	private static final long serialVersionUID = -4475203896747543515L;

	private List<InvoiceSubCategoryDto> invoiceSubCategory;

	public List<InvoiceSubCategoryDto> getInvoiceSubCategory() {
		if (invoiceSubCategory == null)
			invoiceSubCategory = new ArrayList<InvoiceSubCategoryDto>();
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(List<InvoiceSubCategoryDto> invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	@Override
	public String toString() {
		return "InvoiceSubCategoriesDto [invoiceSubCategory=" + invoiceSubCategory + "]";
	}

}
