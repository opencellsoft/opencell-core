package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.InvoiceSubCategoryDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetInvoiceSubCategoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceSubCategoryResponse extends BaseResponse {

	private static final long serialVersionUID = 4992963476297361310L;

	private InvoiceSubCategoryDto invoiceSubCategory;

	public InvoiceSubCategoryDto getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(InvoiceSubCategoryDto invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	@Override
	public String toString() {
		return "GetInvoiceSubCategoryResponse [invoiceSubCategory=" + invoiceSubCategory + ", toString()="
				+ super.toString() + "]";
	}

}
