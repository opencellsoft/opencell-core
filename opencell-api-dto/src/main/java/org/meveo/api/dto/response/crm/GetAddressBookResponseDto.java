package org.meveo.api.dto.response.crm;

import org.meveo.api.dto.crm.AddressBookDto;
import org.meveo.api.dto.response.BaseResponse;

public class GetAddressBookResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8223322259965988450L;
	
	private AddressBookDto addressBook;

	public AddressBookDto getAddressBook() {
		return addressBook;
	}

	public void setAddressBook(AddressBookDto addressBook) {
		this.addressBook = addressBook;
	}
}
