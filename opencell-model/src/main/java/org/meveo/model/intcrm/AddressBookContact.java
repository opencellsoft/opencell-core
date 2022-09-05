package org.meveo.model.intcrm;

import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.communication.contact.Contact;

import javax.persistence.*;

@Entity
@Table(name = "crm_address_book_contact")
public class AddressBookContact extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_book_id", nullable = false)
    private AddressBook addressBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Column(name = "position")
    private String position;

    @Column(name = "main_contact")
    @Type(type = "numeric_boolean")
    protected Boolean mainContact = Boolean.FALSE;

    public AddressBookContact() {
    }

    public AddressBookContact(AddressBook addressBook, Contact contact, String position, Boolean mainContact) {
        this.addressBook = addressBook;
        this.contact = contact;
        this.position = position;
        this.mainContact = mainContact;
    }

    public AddressBook getAddressBook() {
        return addressBook;
    }

    public void setAddressBook(AddressBook addressBook) {
        this.addressBook = addressBook;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getMainContact() {
        return mainContact;
    }

    public void setMainContact(Boolean mainContact) {
        this.mainContact = mainContact;
    }
}
