package org.meveo.api.dto.invoice;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.InvoiceTypeDto;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "InvoiceSubTotals")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceSubTotalsDto {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    @XmlElement(required = true)
    @Schema(description = "The Invoice Type")
    private InvoiceTypeDto invoiceType;
    
    @XmlElement(required = true)
    @Schema(description = "The sub Totals")
    private List<SubTotalsDto> subTotals;

    public InvoiceTypeDto getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceTypeDto invoiceType) {
        this.invoiceType = invoiceType;
    }

    public List<SubTotalsDto> getSubTotals() {
        return subTotals;
    }

    public void setSubTotals(List<SubTotalsDto> subTotals) {
        this.subTotals = subTotals;
    }
}
