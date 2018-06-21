package org.meveo.api.dto.crm;

import java.io.Serializable;
import java.util.List;

public class ContactGroupsDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1403636007806985747L;

	private Long totalNumberOfRecords;
	
	List<ContactGroupDto> contactGroupDto;
	
	public ContactGroupsDto() {
		
	}

	public Long getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	public List<ContactGroupDto> getContactGroupDto() {
		return contactGroupDto;
	}

	public void setContactGroupDto(List<ContactGroupDto> contactGroupDto) {
		this.contactGroupDto = contactGroupDto;
	}
}
