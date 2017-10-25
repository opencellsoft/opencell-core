package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.invoice.InvoiceDto;

@XmlRootElement(name = "Invoices")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoicesDto extends SearchResponse {

    private static final long serialVersionUID = -954637537391623233L;

    private List<InvoiceDto> invoices = new ArrayList<>();

    public List<InvoiceDto> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<InvoiceDto> invoices) {
        this.invoices = invoices;
    }
}