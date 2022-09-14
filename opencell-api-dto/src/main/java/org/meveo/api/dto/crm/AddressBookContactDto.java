package org.meveo.api.dto.crm;

import org.meveo.api.dto.IEntityDto;
import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.dto.account.NameDto;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;

import java.util.Collections;
import java.util.Map;

public class AddressBookContactDto implements IEntityDto {
    private Long id;
    private Map<String, Long> addressBook;
    private String position;
    protected Boolean mainContact = Boolean.FALSE;
    private CustomerDto customer;

    public AddressBookContactDto(Long id, AddressBook addressBook, String position, Boolean mainContact, Customer customer) {
        this.id = id;
        this.addressBook = Collections.singletonMap("id", addressBook.getId());
        this.position = position;
        this.mainContact = mainContact;
        if(customer != null){
            this.customer = new CustomerDto(customer);
            this.customer.setContactInformation(null);
            this.customer.setSeller(null);
            this.customer.setCustomerCategory(null);
            this.customer.setCustomerAccounts(null);
            this.customer.setAdditionalDetails(null);
            this.customer.setIsCompany(null);
            if(customer.getName() != null){
                this.customer.setName(new NameDto(customer.getName()));
            }
        }
    }

    public Map<String, Long> getAddressBook() {
        return addressBook;
    }

    public void setAddressBook(Map<String, Long> addressBook) {
        this.addressBook = addressBook;
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public CustomerDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDto customer) {
        this.customer = customer;
    }
}
