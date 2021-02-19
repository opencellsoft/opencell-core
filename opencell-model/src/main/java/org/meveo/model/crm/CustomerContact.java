package org.meveo.model.crm;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.communication.contact.Contact;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "bi_customers_contacts")
@IdClass(CustomerContactId.class)
public class CustomerContact {

    @Id
    @ManyToOne
    @JoinColumn(name ="contact_id", referencedColumnName = "id")
    private Contact contact;

    @Id
    @ManyToOne
    @JoinColumn(name ="customer_id",referencedColumnName = "id")
    private Customer customer;

    @Column(name = "role")
    private String role;

    public CustomerContact() {
    }

    public CustomerContact(Contact contact, Customer customer, String role) {
        this.contact = contact;
        this.customer = customer;
        this.role = role;
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
