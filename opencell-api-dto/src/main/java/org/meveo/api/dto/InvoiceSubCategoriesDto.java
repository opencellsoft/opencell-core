package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class InvoiceSubCategoriesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "InvoiceSubCategories")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSubCategoriesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4475203896747543515L;

    /** The invoice sub category. */
    private List<InvoiceSubCategoryDto> invoiceSubCategory;

    /**
     * Gets the invoice sub category.
     *
     * @return the invoice sub category
     */
    public List<InvoiceSubCategoryDto> getInvoiceSubCategory() {
        if (invoiceSubCategory == null)
            invoiceSubCategory = new ArrayList<InvoiceSubCategoryDto>();
        return invoiceSubCategory;
    }

    /**
     * Sets the invoice sub category.
     *
     * @param invoiceSubCategory the new invoice sub category
     */
    public void setInvoiceSubCategory(List<InvoiceSubCategoryDto> invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InvoiceSubCategoriesDto [invoiceSubCategory=" + invoiceSubCategory + "]";
    }

}
