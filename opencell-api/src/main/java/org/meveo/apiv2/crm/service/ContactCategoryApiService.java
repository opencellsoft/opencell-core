package org.meveo.apiv2.crm.service;

import java.util.UUID;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.inject.Inject;

import org.hibernate.exception.ConstraintViolationException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.crm.ContactCategoryDto;
import org.meveo.apiv2.crm.mapper.ContactCategoryMapper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.communication.contact.ContactCategory;
import org.meveo.service.intcrm.impl.ContactCategoryService;

@Stateless
public class ContactCategoryApiService extends BaseApi {

	@Inject
	private ContactCategoryService contactCategoryService;
	
	private ContactCategoryMapper mapper = new ContactCategoryMapper();
	
	@TransactionAttribute
	public ContactCategory create(ContactCategoryDto postData) {
		log.info("Delete ContactCategory code={} - description={}", postData.getCode(), postData.getDescription());
		
		ContactCategory searchContactCategory = contactCategoryService.findByCode(postData.getCode());
		if(searchContactCategory != null) {
			throw new EntityAlreadyExistsException(ContactCategory.class, postData.getCode());
		}
		
		ContactCategory entity = mapper.toEntity(postData);
		if(StringUtils.isBlank(entity.getCode())) {
			entity.setCode(UUID.randomUUID().toString());
		}

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

	@TransactionAttribute
	public ContactCategory update(String contactCategoryCode, ContactCategoryDto postData) {
		log.info("Delete ContactCategory code={}", contactCategoryCode);
		ContactCategory searchContactCategory = contactCategoryService.findByCode(contactCategoryCode);
		if(searchContactCategory == null) {
			throw new EntityDoesNotExistsException(ContactCategory.class, contactCategoryCode);
		}
		
		searchContactCategory.setDescription(postData.getDescription());

        try {
            populateCustomFields(postData.getCustomFields(), searchContactCategory, false, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        contactCategoryService.update(searchContactCategory);
		
		return searchContactCategory;
	}

	@TransactionAttribute
	public void delete(String contactCategoryCode) {
		log.info("Delete ContactCategory code={}", contactCategoryCode);
		ContactCategory searchContactCategory = contactCategoryService.findByCode(contactCategoryCode);
		if(searchContactCategory == null) {
			throw new EntityDoesNotExistsException(ContactCategory.class, contactCategoryCode);
		}
		contactCategoryService.remove(searchContactCategory);
	}

}
