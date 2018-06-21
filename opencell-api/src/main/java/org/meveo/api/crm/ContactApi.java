package org.meveo.api.crm;

import java.io.IOException;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.account.AccountEntityApi;
import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.dto.crm.ContactsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.crm.ContactsResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Country;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.intcrm.impl.AddressBookService;
import org.meveo.service.intcrm.impl.ContactService;
import org.primefaces.model.SortOrder;

@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class ContactApi extends AccountEntityApi {

	@Inject
	ContactService contactService;
    
	@Inject
	TitleService titleService;
	
	@Inject
	AddressBookService addressBookService;
	
	@Inject
	CustomerService customerService;

	public Contact create(ContactDto postData) throws MeveoApiException, BusinessException {
	    if (StringUtils.isBlank(postData.getName().getFirstName())) {
	        missingParameters.add("firstName");
	    }

	    if (StringUtils.isBlank(postData.getName().getLastName())) {
	        missingParameters.add("lastName");
	    }
	    
	    if (StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {
	        missingParameters.add("email");
	        missingParameters.add("code");
	    }
	    

        handleMissingParameters();
        
        Contact contact = new Contact();
        populate(postData, contact);
        
        
                
        if(postData.getCode().length() == 0)
        	contact.setCode(postData.getEmail());
        else if (postData.getEmail().length() == 0)
        	contact.setEmail(postData.getCode());
        
        contact.setCompany(postData.getCompany());
        contact.setDescription(postData.getDescription());
        contact.setMobile(postData.getMobile());
        contact.setPhone(postData.getPhone());
        contact.setAssistantName(postData.getAssistantName());
        contact.setAssistantPhone(postData.getAssistantPhone());
        contact.setPosition(postData.getPosition());
        contact.setSocialIdentifier(postData.getSocialIdentifier());
        contact.setWebsiteUrl(postData.getWebsiteUrl());
        contact.setVip(postData.isVip());
        contact.setProspect(postData.isProspect());
        contact.setAgreedToUA(postData.isAgreedToUA());
	
        contactService.create(contact);

		addressBookService.addContact(null, contact);
		
        return contact;
    }
	
	
	public Contact update(ContactDto postData) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getName().getFirstName())) {
	        missingParameters.add("firstName");
	    }

	    if (StringUtils.isBlank(postData.getName().getLastName())) {
	        missingParameters.add("lastName");
	    }
	    
	    String code = postData.getCode();
	    if (StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(code)) {
	        missingParameters.add("email");
	        missingParameters.add("code");
	    }
	    

        handleMissingParameters();
        

        Contact contact = contactService.findByCode(postData.getCode());
		
        if(contact == null) {
        	throw new EntityDoesNotExistsException(Contact.class, code, "code");
        }
        
        updateAccount(contact, postData);
        
        if (!StringUtils.isBlank(postData.getCompany())) {
        	contact.setCompany(postData.getCompany());
        }
        
        if (!StringUtils.isBlank(postData.getCompany())) {
        	contact.setCompany(postData.getCompany());
        }
        
        if (!StringUtils.isBlank(postData.getDescription())) {
        	contact.setDescription(postData.getDescription());
        }
        
        if (!StringUtils.isBlank(postData.getMobile())) {
        	contact.setMobile(postData.getMobile());
        }
        
        if (!StringUtils.isBlank(postData.getPhone())) {
        	contact.setPhone(postData.getPhone());
        }
        
        if (!StringUtils.isBlank(postData.getAssistantName())) {
        	contact.setAssistantName(postData.getAssistantName());
        }
        
        if (!StringUtils.isBlank(postData.getAssistantPhone())) {
        	contact.setAssistantPhone(postData.getAssistantPhone());
        }
        
        if (!StringUtils.isBlank(postData.getPosition())) {
        	contact.setPosition(postData.getPosition());
        }
        
        if (!StringUtils.isBlank(postData.getSocialIdentifier())) {
	        contact.setSocialIdentifier(postData.getSocialIdentifier());
        }
        
        if (!StringUtils.isBlank(postData.getWebsiteUrl())) {
	        contact.setWebsiteUrl(postData.getWebsiteUrl());
        }

        
        if (!StringUtils.isBlank(postData.isVip())) {
	        contact.setVip(postData.isVip());
        }
        
        if (!StringUtils.isBlank(postData.isProspect())) {
	        contact.setProspect(postData.isProspect());
        }
        
        if (!StringUtils.isBlank(postData.isAgreedToUA())) {
	        contact.setAgreedToUA(postData.isAgreedToUA());
        }
        
        contact =  contactService.update(contact);
        return contact;
	}
	
	public Contact createOrUpdate(ContactDto postData) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getName().getFirstName())) {
	        missingParameters.add("firstName");
	    }

	    if (StringUtils.isBlank(postData.getName().getLastName())) {
	        missingParameters.add("lastName");
	    }
	    
	    String code = postData.getCode();
	    if (StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(code)) {
	        missingParameters.add("email");
	        missingParameters.add("code");
	    }
	    

        handleMissingParameters();
        
        Contact contact = contactService.findByCode(postData.getCode());
        
        if(contact == null) {
        	return create(postData);
        }
        else {
        	return update(postData);
        }
	}
	
	public void remove(String code) throws BusinessException, EntityDoesNotExistsException {
		Contact contact = contactService.findByCode(code);
		
		if(contact == null) {
            throw new EntityDoesNotExistsException(Contact.class, code, "code");
		}
		
		contactService.remove(contact);
	}
	
	public ContactDto findByCode(String code) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			 missingParameters.add("code");
		}
		
        handleMissingParameters();

        ContactDto contactDto = null;
        Contact contact = contactService.findByCode(code);
        
        if(contact == null) {
        	throw new EntityDoesNotExistsException(Contact.class, code, "code");
        }
        
        contactDto = new ContactDto(contact);
        
        return contactDto;
	}
	
	@SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "contacts.contact", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = Contact.class) })
    public ContactsResponseDto list(ContactDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        return list(postData, pagingAndFiltering, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }
	
	@SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "contacts.contact", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = Contact.class) })
    public ContactsResponseDto list(ContactDto postData, PagingAndFiltering pagingAndFiltering, CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        if (postData != null) {
            pagingAndFiltering.addFilter("code", postData.getCode());
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, Contact.class);

        Long totalCount = contactService.count(paginationConfig);

        ContactsDto contactsDto = new ContactsDto();
        ContactsResponseDto result = new ContactsResponseDto();

        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        contactsDto.setTotalNumberOfRecords(totalCount);

        if (totalCount > 0) {
            List<Contact> contacts = contactService.list(paginationConfig);
            for (Contact c : contacts) {
                contactsDto.getContact().add(new ContactDto(c));
            }
        }
        result.setContacts(contactsDto);
        return result;
    }

	public void importLinkedInFromText(String context) {
		try {
			contactService.parseLinkedInFromText(context);
		} catch (IOException | BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
