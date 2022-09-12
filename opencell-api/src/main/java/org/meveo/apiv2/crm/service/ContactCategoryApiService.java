package org.meveo.apiv2.crm.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.crm.ContactCategoryDto;
import org.meveo.apiv2.crm.mapper.ContactCategoryMapper;
import org.meveo.model.communication.contact.ContactCategory;
import org.meveo.service.intcrm.impl.ContactCategoryService;

@Stateless
public class ContactCategoryApiService extends BaseApi {

	@Inject
	private ContactCategoryService contactCategoryService;
	
	private ContactCategoryMapper mapper = new ContactCategoryMapper();
	
	@TransactionAttribute
	public ContactCategory create(ContactCategoryDto postData) {
		
		ContactCategory searchContactCategory = contactCategoryService.findByCode(postData.getCode());
		if(searchContactCategory != null) {
			throw new EntityAlreadyExistsException(ContactCategory.class, postData.getCode());
		}
		
		ContactCategory entity = mapper.toEntity(postData);

        try {
            populateCustomFields(postData.getCustomFields(), entity, true, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        contactCategoryService.create(entity);
		
		return entity;
	}

}
