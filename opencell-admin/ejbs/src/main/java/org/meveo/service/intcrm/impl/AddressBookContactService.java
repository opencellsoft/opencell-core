package org.meveo.service.intcrm.impl;

import org.meveo.model.communication.contact.Contact;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.intcrm.AddressBookContact;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class AddressBookContactService extends PersistenceService<AddressBookContact> {

    public boolean hasMainContact(AddressBook addressBook){
        return !getMainContact(addressBook.getId()).isEmpty();
    }

    public List<AddressBookContact> getMainContact(Long addressBookId){
        return getEntityManager().createNamedQuery("AddressBookContact.findAddressBookMainContact", AddressBookContact.class)
                .setParameter("addressBookId", addressBookId)
                .getResultList();
    }

    public List<AddressBookContact> findByContact(Contact contact) {
        return getEntityManager().createNamedQuery("AddressBookContact.findAddressBookContactByContact", AddressBookContact.class)
                .setParameter("contactId", contact.getId())
                .getResultList();
    }
}
