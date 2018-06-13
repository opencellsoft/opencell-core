package org.meveo.api.crm;

import java.io.IOException;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
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
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.intcrm.impl.ContactService;
import org.primefaces.model.SortOrder;
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class ContactApi extends BaseApi {

	@Inject
	ContactService contactService;
    
	@Inject
	TitleService titleService;

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
        
    	Title title = titleService.findByCode("MR");
    	String firstName = postData.getName().getFirstName();
    	String lastName = postData.getName().getLastName();
        Name name = new Name(title, firstName, lastName);
        contact.setName(name);
        
        String email = postData.getEmail();
        String code = postData.getCode();
        
        if(code.length() == 0) code = email;
        else if (email.length() == 0) email = code;
        contact.setEmail(email);
        contact.setCode(code);
        
        if(postData.getAddress() != null) {
            Country country = new Country();
            String address1 = postData.getAddress().getAddress1();
            String address2 = postData.getAddress().getAddress2();
            String address3 = postData.getAddress().getAddress3();
            String zipCode = postData.getAddress().getZipCode();
            String city = postData.getAddress().getCity();
            String state = postData.getAddress().getState();
            Address address = new Address(address1, address2, address3, zipCode, city , country, state);
            contact.setAddress(address);
        }
        
        String assistantName = postData.getAssistantName();
        String assistantPhone = postData.getAssistantPhone();
        contact.setAssistantName(assistantName);
        contact.setAssistantPhone(assistantPhone);
        
        String position = postData.getPosition();
        contact.setPosition(position);
        
        String socialIdentifier = postData.getSocialIdentifier();
        String websiteUrl = postData.getWebsiteUrl();
        contact.setSocialIdentifier(socialIdentifier);
        contact.setWebsiteUrl(websiteUrl);
        
        Boolean isVip = postData.isVip();
        Boolean isSuspect = postData.isSuspect();
        Boolean agreedToUA = postData.isAgreedToUA();
        contact.setVip(isVip);
        contact.setSuspect(isSuspect);
        contact.setAgreedToUA(agreedToUA);
        
        contactService.create(contact);
        
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
        
        
        if(postData.getAddress() != null) {
            Country country = new Country();
            String address1 = postData.getAddress().getAddress1();
            String address2 = postData.getAddress().getAddress2();
            String address3 = postData.getAddress().getAddress3();
            String zipCode = postData.getAddress().getZipCode();
            String city = postData.getAddress().getCity();
            String state = postData.getAddress().getState();
            Address address = new Address(address1, address2, address3, zipCode, city , country, state);
            contact.setAddress(address);
        }
        
        
        String assistantName = postData.getAssistantName();
        String assistantPhone = postData.getAssistantPhone();
        if (assistantName != null) contact.setAssistantName(assistantName);
        if (assistantPhone != null) contact.setAssistantPhone(assistantPhone);
        
        String position = postData.getPosition();
        if (position != null) contact.setPosition(position);
        
        String socialIdentifier = postData.getSocialIdentifier();
        String websiteUrl = postData.getWebsiteUrl();
        if (socialIdentifier != null) contact.setSocialIdentifier(socialIdentifier);
        if (websiteUrl != null) contact.setWebsiteUrl(websiteUrl);
        
        Boolean isVip = postData.isVip();
        Boolean isSuspect = postData.isSuspect();
        Boolean agreedToUA = postData.isAgreedToUA();
        if (isVip != null) contact.setVip(isVip);
        if (isSuspect != null) contact.setSuspect(isSuspect);
        if (agreedToUA != null) contact.setAgreedToUA(agreedToUA);
        
        return contactService.update(contact);
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
