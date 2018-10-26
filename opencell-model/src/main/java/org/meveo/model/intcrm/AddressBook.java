package org.meveo.model.intcrm;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.communication.contact.Contact;

/**
 * Address book
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ExportIdentifier({ "addressbook" })
@Table(name = "crm_address_book")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_address_book_seq"), })
public class AddressBook extends BusinessEntity {

    private static final long serialVersionUID = 6638793926019456947L;

    /**
     * Contacts in the address book
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "addressBook", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Contact> contacts = new HashSet<Contact>();

    public AddressBook() {

    }

    public AddressBook(String code) {
        this.setCode(code);

    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

}
