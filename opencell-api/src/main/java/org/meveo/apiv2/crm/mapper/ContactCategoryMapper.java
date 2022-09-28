package org.meveo.apiv2.crm.mapper;

import org.meveo.apiv2.crm.ContactCategoryDto;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.communication.contact.ContactCategory;

public class ContactCategoryMapper extends ResourceMapper<ContactCategoryDto, ContactCategory> {

	@Override
	public ContactCategoryDto toResource(ContactCategory entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContactCategory toEntity(ContactCategoryDto resource) {
		var contactCategory = new ContactCategory();
		
		contactCategory.setCode(resource.getCode());
		contactCategory.setDescription(resource.getDescription());
		
		
		return contactCategory;
	}

}