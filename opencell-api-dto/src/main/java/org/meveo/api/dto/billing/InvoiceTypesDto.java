package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class InvoiceTypesDto.
 */
@XmlRootElement(name = "InvoiceTypes")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceTypesDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The invoice types. */
    private List<InvoiceTypeDto> invoiceTypes = new ArrayList<InvoiceTypeDto>();

    /**
     * Instantiates a new invoice types dto.
     */
    public InvoiceTypesDto() {

    }

    /**
     * Gets the invoice types.
     *
     * @return the invoiceTypes
     */
    public List<InvoiceTypeDto> getInvoiceTypes() {
        return invoiceTypes;
    }

    /**
     * Sets the invoice types.
     *
     * @param invoiceTypes the invoiceTypes to set
     */
    public void setInvoiceTypes(List<InvoiceTypeDto> invoiceTypes) {
        this.invoiceTypes = invoiceTypes;
    }

    @Override
    public String toString() {
        return "InvoiceTypesDto [InvoiceTypes=" + invoiceTypes + "]";
    }

}