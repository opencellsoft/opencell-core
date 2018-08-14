package org.meveo.api.dto.crm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactsDto implements Serializable {

	private static final long serialVersionUID = 794745468423515355L;
	
	private List<ContactDto> contact;

	private Long totalNumberOfRecords;

	public ContactsDto() {
		
	}
	
	public List<ContactDto> getContact() {
		if (contact == null) {
            contact = new ArrayList<ContactDto>();
        }

		return contact;
	}

	public void setContact(List<ContactDto> contacts) {
		this.contact = contacts;
	}
    
	
	public Long getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}
	
	public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
	}

	public String toString() {
		return "CustomersDto [customer=" + contact + "]";
	}

}
