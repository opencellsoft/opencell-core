package org.meveo.api.dto.invoice;

import org.meveo.api.dto.BaseEntityDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class CancelInvoiceRequestDto.
 *
 * @author Thang Nguyen
 */
@XmlRootElement(name = "CancelInvoiceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CancelInvoiceRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4477259461644796968L;

    /** The invoiceId. */
    @XmlElement(required = true)
    private Long invoiceId;

    /**
     * Gets the invoice id.
     *
     * @return the invoice id
     */
    public Long getInvoiceId() {
        return invoiceId;
    }

    /**
     * Sets the invoice Id.
     *
     * @param invoiceId the new invoice id
     */
    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
}
