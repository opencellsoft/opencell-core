package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.InvoiceSubCategoryCountryDto;

/**
 * The Class GetInvoiceSubCategoryCountryResponse.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetInvoiceSubCategoryCountryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoiceSubCategoryCountryResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5831917945471936382L;

    /** The invoice sub category country dto. */
    private InvoiceSubCategoryCountryDto invoiceSubCategoryCountryDto;

    /**
     * Gets the invoice sub category country dto.
     *
     * @return the invoice sub category country dto
     */
    public InvoiceSubCategoryCountryDto getInvoiceSubCategoryCountryDto() {
        return invoiceSubCategoryCountryDto;
    }

    /**
     * Sets the invoice sub category country dto.
     *
     * @param invoiceSubCategoryCountryDto the new invoice sub category country dto
     */
    public void setInvoiceSubCategoryCountryDto(InvoiceSubCategoryCountryDto invoiceSubCategoryCountryDto) {
        this.invoiceSubCategoryCountryDto = invoiceSubCategoryCountryDto;
    }

    @Override
    public String toString() {
        return "GetInvoiceSubCategoryCountryResponse [invoiceSubCategoryCountryDto=" + invoiceSubCategoryCountryDto + ", toString()=" + super.toString() + "]";
    }
}