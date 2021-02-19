package org.meveo.model.crm;


import org.meveo.model.communication.contact.Contact;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Embeddable
public class ContactCustomerId {

    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "customer_id")
    private Long customerId;

    public ContactCustomerId() {
    }

    public ContactCustomerId(Long contactId, Long customerId) {
        this.contactId = contactId;
        this.customerId = customerId;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
