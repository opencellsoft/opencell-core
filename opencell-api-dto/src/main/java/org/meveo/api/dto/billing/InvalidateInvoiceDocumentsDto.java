package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

@XmlRootElement(name = "InvalidateInvoiceDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvalidateInvoiceDocumentsDto extends BaseEntityDto {

    private static final long serialVersionUID = 2167574427839928842L;

    private Boolean invalidateXMLInvoices;

    private Boolean invalidatePDFInvoices;

    /**
     * @return the invalidateXMLInvoices
     */
    public Boolean getInvalidateXMLInvoices() {
        return invalidateXMLInvoices;
    }

    /**
     * @param invalidateXMLInvoices the invalidateXMLInvoices to set
     */
    public void setInvalidateXMLInvoices(Boolean invalidateXMLInvoices) {
        this.invalidateXMLInvoices = invalidateXMLInvoices;
    }

    /**
     * @return the invalidatePDFInvoices
     */
    public Boolean getInvalidatePDFInvoices() {
        return invalidatePDFInvoices;
    }

    /**
     * @param invalidatePDFInvoices the invalidatePDFInvoices to set
     */
    public void setInvalidatePDFInvoices(Boolean invalidatePDFInvoices) {
        this.invalidatePDFInvoices = invalidatePDFInvoices;
    }
}