package org.meveo.model.billing;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class LinkedInvoiceId implements Serializable {


    public static final int NB_PRECISION = 23;
    public static final int NB_DECIMALS = 12;
    
    private Invoice id;
    private Invoice linkedInvoice;
    


    public LinkedInvoiceId(Invoice id, Invoice linkedInvoice) {
        super();
        this.id = id;
        this.linkedInvoice = linkedInvoice;
    }

    
    public LinkedInvoiceId() {
        
    }
    
    public Invoice getId() {
        return id;
    }

    public void setId(Invoice id) {
        this.id = id;
    }

    public Invoice getLinkedInvoice() {
        return linkedInvoice;
    }

    public void setLinkedInvoice(Invoice linkedInvoice) {
        this.linkedInvoice = linkedInvoice;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, linkedInvoice);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LinkedInvoiceId other = (LinkedInvoiceId) obj;
        return Objects.equals(id, other.id) && Objects.equals(linkedInvoice, other.linkedInvoice);
    }

    
}
