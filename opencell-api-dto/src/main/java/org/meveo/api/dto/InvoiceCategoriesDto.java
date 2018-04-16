package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class InvoiceCategoriesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "InvoiceCategories")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceCategoriesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7706480655782126035L;

    /** The invoice category. */
    private List<InvoiceCategoryDto> invoiceCategory;

    /**
     * Gets the invoice category.
     *
     * @return the invoice category
     */
    public List<InvoiceCategoryDto> getInvoiceCategory() {
        if (invoiceCategory == null)
            invoiceCategory = new ArrayList<InvoiceCategoryDto>();
        return invoiceCategory;
    }

    /**
     * Sets the invoice category.
     *
     * @param invoiceCategory the new invoice category
     */
    public void setInvoiceCategory(List<InvoiceCategoryDto> invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InvoiceCategoriesDto [invoiceCategory=" + invoiceCategory + "]";
    }

}
