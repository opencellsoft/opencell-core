package org.meveo.api.dto.dunning;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.model.billing.Invoice;
import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;

/**
 * DTO equivalent of DunningDocument entity.
 * 
 * @author abdelmounaim akadid
 */
@XmlRootElement(name = "DunningDocument")
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningDocumentDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1897384994086770497L;
    
    /**
     * DunningDocument Id
     */
    protected String dunningDocumentId;
    
    /**
     * Customer account Code
     */
    protected String customerAccountCode;

    /**
     * Subscription Code
     */
    protected String subscriptionCode;

    /** List of payments. */
    @XmlElementWrapper
    @XmlElement(name = "payment", required = true)
    protected List<PaymentDto> payments = new ArrayList<PaymentDto>();

    /** The tax aggregates */
    @XmlElementWrapper
    @XmlElement(name = "dueInvoice", required = true)
    protected List<InvoiceDto> dueInvoices = new ArrayList<InvoiceDto>();
    
    /**
     * Instantiates a new dunningDocument dto.
     */
    public DunningDocumentDto() {
        super();
    }
    
    /**
     * Instantiates a new dunningDocument dto.
     * 
     * @param dunningDocument DunningDocument entity
     */
    public DunningDocumentDto(DunningDocument dunningDocument) {
        super(dunningDocument);

        if(dunningDocument.getCustomerAccount() != null) {
            setCustomerAccountCode(dunningDocument.getCustomerAccount().getCode());
        }
        if(dunningDocument.getSubscription() != null) {
            setSubscriptionCode(dunningDocument.getSubscription().getCode());
        }
        if(dunningDocument.getDueInvoices() != null) {
            List<InvoiceDto> dueInvoices = new ArrayList<InvoiceDto>();
            for(RecordedInvoice recordedInvoice : dunningDocument.getDueInvoices()) {
                for(Invoice invoice : recordedInvoice.getInvoices()) {
                    dueInvoices.add(new InvoiceDto(invoice, false));
                }
                
            }
            setDueInvoices(dueInvoices);
        }
        
        if(dunningDocument.getPayments() != null) {
            List<PaymentDto> paymentDtos = new ArrayList<PaymentDto>();
            for(Payment payment : dunningDocument.getPayments()) {
                 paymentDtos.add(new PaymentDto(payment));
            }
            setPayments(paymentDtos);
        }        
    }

    /**
     * @return the customerAccountCode
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    /**
     * @param customerAccountCode the customerAccountCode to set
     */
    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    /**
     * @return the subscriptionCode
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * @param subscriptionCode the subscriptionCode to set
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    /**
     * @return the payments
     */
    public List<PaymentDto> getPayments() {
        return payments;
    }

    /**
     * @param payments the payments to set
     */
    public void setPayments(List<PaymentDto> payments) {
        this.payments = payments;
    }

    /**
     * @return the dueInvoices
     */
    public List<InvoiceDto> getDueInvoices() {
        return dueInvoices;
    }

    /**
     * @param dueInvoices the dueInvoices to set
     */
    public void setDueInvoices(List<InvoiceDto> dueInvoices) {
        this.dueInvoices = dueInvoices;
    }

    /**
     * @return the dunningDocumentId
     */
    public String getDunningDocumentId() {
        return dunningDocumentId;
    }

    /**
     * @param dunningDocumentId the dunningDocumentId to set
     */
    public void setDunningDocumentId(String dunningDocumentId) {
        this.dunningDocumentId = dunningDocumentId;
    }
    
}