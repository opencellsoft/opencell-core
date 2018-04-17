package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.InvoiceCategoryDto;

/**
 * The Class GetInvoiceCategoryResponse.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetInvoiceCategoryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceCategoryResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8132109724455311508L;

    /** The invoice category. */
    private InvoiceCategoryDto invoiceCategory;

    /**
     * Gets the invoice category.
     *
     * @return the invoice category
     */
    public InvoiceCategoryDto getInvoiceCategory() {
        return invoiceCategory;
    }

    /**
     * Sets the invoice category.
     *
     * @param invoiceCategory the new invoice category
     */
    public void setInvoiceCategory(InvoiceCategoryDto invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }

    @Override
    public String toString() {
        return "GetInvoiceCategoryResponse [invoiceCategory=" + invoiceCategory + ", toString()=" + super.toString() + "]";
    }
}