package org.meveo.api.dto.crm;

import java.util.Map;

public class AddressBookContactDto {
    private Map<String, Long> AddressBook;
    private String position;
    protected Boolean mainContact = Boolean.FALSE;

    public Map<String, Long> getAddressBook() {
        return AddressBook;
    }

    public void setAddressBook(Map<String, Long> addressBook) {
        AddressBook = addressBook;
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
