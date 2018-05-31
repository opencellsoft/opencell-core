package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.invoice.InvoiceDto;

/**
 * The Class InvoicesDto.
 */
@XmlRootElement(name = "Invoices")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoicesDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -954637537391623233L;

    /** The invoices. */
    private List<InvoiceDto> invoices = new ArrayList<>();

    /**
     * Gets the invoices.
     *
     * @return the invoices
     */
    public List<InvoiceDto> getInvoices() {
        return invoices;
    }

    /**
     * Sets the invoices.
     *
     * @param invoices the new invoices
     */
    public void setInvoices(List<InvoiceDto> invoices) {
        this.invoices = invoices;
    }
}