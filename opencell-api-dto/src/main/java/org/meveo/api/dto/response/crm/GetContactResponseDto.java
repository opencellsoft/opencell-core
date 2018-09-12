package org.meveo.api.dto.response.crm;

import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.dto.response.BaseResponse;

public class GetContactResponseDto extends BaseResponse {

	private static final long serialVersionUID = 570176017147951808L;
	
	private ContactDto contact;
    
    
    public ContactDto getContact() {
		return contact;
	}


	public void setContact(ContactDto contact) {
		this.contact = contact;
	}


	@Override
    public String toString() {
    	return "GetContactResponse [contact=" + contact + ", toString()=" + super.toString() + "]";
    }
}
