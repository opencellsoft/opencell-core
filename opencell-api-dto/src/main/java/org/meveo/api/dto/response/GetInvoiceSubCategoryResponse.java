package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.InvoiceSubCategoryDto;

/**
 * The Class GetInvoiceSubCategoryResponse.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetInvoiceSubCategoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceSubCategoryResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4992963476297361310L;

    /** The invoice sub category. */
    private InvoiceSubCategoryDto invoiceSubCategory;

    /**
     * Gets the invoice sub category.
     *
     * @return the invoice sub category
     */
    public InvoiceSubCategoryDto getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    /**
     * Sets the invoice sub category.
     *
     * @param invoiceSubCategory the new invoice sub category
     */
    public void setInvoiceSubCategory(InvoiceSubCategoryDto invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    @Override
    public String toString() {
        return "GetInvoiceSubCategoryResponse [invoiceSubCategory=" + invoiceSubCategory + ", toString()=" + super.toString() + "]";
    }
}