package org.meveo.api.dto.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.meveo.api.dto.BaseDto;


@XmlRootElement(name = "GetPdfInvoiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetPdfInvoiceRequestDto extends BaseDto {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String invoiceNumber;
    private String invoiceType;

    public GetPdfInvoiceRequestDto() {

    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    @Override
    public String toString() {
        return "GetPdfInvoiceRequestDto [invoiceNumber=" + invoiceNumber + ", invoiceType=" + invoiceType + "]";
    }


}
