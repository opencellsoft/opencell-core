package org.meveo.api.dto.crm;

import java.util.List;

public class CustomerContactDtos {

    private List<CustomerContactDto> customersContact;

    public List<CustomerContactDto> getCustomersContact() {
        return customersContact;
    }

    public void setCustomersContact(List<CustomerContactDto> customersContact) {
        this.customersContact = customersContact;
    }
}
