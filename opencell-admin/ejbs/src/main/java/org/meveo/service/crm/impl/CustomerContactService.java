package org.meveo.service.crm.impl;

import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerContact;
import org.meveo.service.base.BaseService;
import org.meveo.service.base.NativePersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class CustomerContactService extends NativePersistenceService {

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    public void create(Contact contact, Customer customer, String role){
        CustomerContact customerContact = new CustomerContact(contact, customer, role);
        contact.getCustomers().add(customerContact);
        customer.getContacts().add(customerContact);
        emWrapper.getEntityManager().persist(customerContact);
    }

    public void removeAll(List<CustomerContact> customerContactList){
        customerContactList.forEach(cc -> emWrapper.getEntityManager().remove(cc));
    }
}
