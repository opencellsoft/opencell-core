package org.meveo.model.crm;


import org.meveo.model.communication.contact.Contact;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "bi_contacts_customers")
public class ContactCustomer {


    @EmbeddedId
    private ContactCustomerId contactCustomerId;

    @ManyToOne
    @MapsId("contactId")
    private Contact contact;

    @ManyToOne
    @MapsId("customerId")
    private Customer customer;

    @Column(name = "role")
    private String role;

    public ContactCustomer(Contact contact, Customer customer) {
        this.contact = contact;
        this.customer = customer;
        this.contactCustomerId = new ContactCustomerId(contact.getId(), customer.getId());
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
