package org.meveo.api.dto.crm;

import org.meveo.api.dto.IEntityDto;

import java.util.Map;

public class AddressBookContactDto implements IEntityDto {
    private Long id;
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

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
