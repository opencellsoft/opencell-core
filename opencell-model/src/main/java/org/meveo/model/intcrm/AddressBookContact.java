package org.meveo.model.intcrm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.communication.contact.Contact;

@Entity
@Table(name = "crm_address_book_contact")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "crm_address_book_contact_seq")})
@NamedQueries({
        @NamedQuery(name = "AddressBookContact.findAddressBookMainContact", query = "select abc from AddressBookContact abc where abc.addressBook.id=:addressBookId and abc.mainContact=true"),
        @NamedQuery(name = "AddressBookContact.findAddressBookContactByContact", query = "select abc from AddressBookContact abc where abc.contact.id=:contactId")
})
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
