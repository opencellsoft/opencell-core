package org.meveo.api.crm;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Country;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.intcrm.impl.ContactService;
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class ContactApi extends BaseApi {

	@Inject
	ContactService contactService;
    
	@Inject
	TitleService titleService;

	public void create(ContactDto postData) throws BusinessException {
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
	    

        try {
			handleMissingParameters();
		} catch (MissingParameterException e) {
			log.debug("Missing parameters");
		}
        
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
        
        Country country = new Country();
        String address1 = postData.getAddress().getAddress1();
        String address2 = postData.getAddress().getAddress2();
        String address3 = postData.getAddress().getAddress3();
        String zipCode = postData.getAddress().getZipCode();
        String city = postData.getAddress().getCity();
        String state = postData.getAddress().getState();
        Address address = new Address(address1, address2, address3, zipCode, city , country, state);
        contact.setAddress(address);
        
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
    }
	
	
	
	public void remove(String code) throws BusinessException, EntityDoesNotExistsException {
		Contact contact = contactService.findByCode(code);
		
		if(contact == null) {
            throw new EntityDoesNotExistsException(Contact.class, code, "code");
		}
		
		contactService.remove(contact);
	}
	
	public Contact findByCode(String code) {
		return contactService.findByCode(code);
	}
	
	public void update(ContactDto postData) {
		// TODO Auto-generated method stub
		
	}
}
