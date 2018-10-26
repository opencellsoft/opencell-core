package org.meveo.api.crm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.account.AccountEntityApi;
import org.meveo.api.dto.account.FilterProperty;
import org.meveo.api.dto.account.FilterResults;
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.dto.crm.ContactsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.crm.ContactsResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethod;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.intcrm.impl.AdditionalDetailsService;
import org.meveo.service.intcrm.impl.AddressBookService;
import org.meveo.service.intcrm.impl.ContactService;
import org.primefaces.model.SortOrder;

@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class ContactApi extends AccountEntityApi {

	@Inject
	ContactService contactService;
	
	@Inject
	SellerService sellerService;

	@Inject
	TitleService titleService;

	@Inject
	AddressBookService addressBookService;
	
	@Inject
	AdditionalDetailsService additionalDetailsService;
	
	@Inject
	CustomerService customerService;
	
	@Inject
	CustomerBrandService customerBrandService;
	
	@Inject
	CustomerCategoryService customerCategoryService;


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
		else if(StringUtils.isBlank(postData.getEmail())) {
			missingParameters.add("email");
		}

		handleMissingParameters();

		Contact contact = new Contact();
		populate(postData, contact);

		if (!StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {	
			contact.setCode(postData.getEmail());
			contact.setEmail(postData.getEmail());
		} else {
			contact.setCode(postData.getCode());
			contact.setEmail(postData.getEmail());
		}

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
		contact.setTags(postData.getTags()	);
		
		Customer customer = null;
		if(contact.getCompany() == null || contact.getCompany().isEmpty()) {
			customer = customerService.findByCompanyName("UNASSIGNED");
			if(customer == null) customer = contactService.createUnassignedCustomer();
			contact.setAddressBook(customer.getAddressbook());
		}
		else {
			customer = customerService.findByCompanyName(contact.getCompany());
			if(customer != null) {
				contact.setAddressBook(customer.getAddressbook());
			}
			else {
				customer = contactService.createCustomerFromContact(contact);
			}
		}
			
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

		if (StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("email");
			missingParameters.add("code");
		}

		handleMissingParameters();
		
		String code = null;
		if (!StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {
			code = postData.getEmail();
		}
		else {
			code = postData.getCode();
		}
		
		Contact contact = contactService.findByCode(code);

		if (contact == null) {
			throw new EntityDoesNotExistsException(Contact.class, code, "code");
		}

		updateAccount(contact, postData);
		contact.setCode(code);
		
		if (!StringUtils.isBlank(postData.getEmail())) {
			contact.setEmail(postData.getEmail());
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
		
		if (!StringUtils.isBlank(postData.getTags())) {
			contact.setTags(postData.getTags());
		}

		if(contact.getCompany() != null && contact.getCompany().equals(postData.getCompany())) {
			contact.setCompany(postData.getCompany());
			Customer customer = null;
			if(contact.getCompany() == null || contact.getCompany().isEmpty()) {
				customer = customerService.findByCompanyName("UNASSIGNED");
				if(customer == null) customer = contactService.createUnassignedCustomer();
				contact.setAddressBook(customer.getAddressbook());
			}
			else {
				customer = customerService.findByCompanyName(contact.getCompany());
				if(customer != null) {
					contact.setAddressBook(customer.getAddressbook());
				}
				else {
					customer = contactService.createCustomerFromContact(contact);
				}
			}
		}
		
		
		contact = contactService.update(contact);
		return contact;
	}

	public Contact createOrUpdate(ContactDto postData) throws MeveoApiException, BusinessException {
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

		String code = null;
		if (!StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {
			code = postData.getEmail();
		}
		else {
			code = postData.getCode();
		}
		
		Contact contact = contactService.findByCode(code);

		if (contact == null) {
			return create(postData);
		} else {
			return update(postData);
		}
	}

	public void remove(String code) throws BusinessException, EntityDoesNotExistsException {
		Contact contact = contactService.findByCode(code);

		if (contact == null) {
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

		if (contact == null) {
			throw new EntityDoesNotExistsException(Contact.class, code, "code");
		}

		contactDto = new ContactDto(contact);

		return contactDto;
	}

	@SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
	@FilterResults(propertyToFilter = "contacts.contact", itemPropertiesToFilter = {
			@FilterProperty(property = "code", entityClass = Contact.class) })
	public ContactsResponseDto list(ContactDto postData, PagingAndFiltering pagingAndFiltering)
			throws MeveoApiException {
		return list(postData, pagingAndFiltering, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
	}

	@SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
	@FilterResults(propertyToFilter = "contacts.contact", itemPropertiesToFilter = {
			@FilterProperty(property = "code", entityClass = Contact.class) })
	public ContactsResponseDto list(ContactDto postData, PagingAndFiltering pagingAndFiltering,
			CustomFieldInheritanceEnum inheritCF) throws MeveoApiException {

		if (pagingAndFiltering == null) {
			pagingAndFiltering = new PagingAndFiltering();
		}

		if (postData != null) {
			pagingAndFiltering.addFilter("code", postData.getCode());
		}

		PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null,
				pagingAndFiltering, Contact.class);

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

	public ContactsDto importCSVText(String context) throws IOException {
		List<Contact> failedToPersist = new ArrayList<Contact>();
		List<Contact> contacts = null;
		List<String> failedToPersistLog = new ArrayList<String>();

		contacts = contactService.parseCSVText(context);
		
		for(Contact contact : contacts) {
			if(StringUtils.isBlank(contact.getName().getFirstName())) {
				missingParameters.add("firstName");
			}
			if(StringUtils.isBlank(contact.getName().getLastName())) {
				missingParameters.add("lastName");
			}
			if(StringUtils.isBlank(contact.getEmail()) && StringUtils.isBlank(contact.getCode())) {
				missingParameters.add("email");
				missingParameters.add("code");
			}
			
			try {
				handleMissingParameters();
				
				Contact c = contactService.findByCode(contact.getCode());
				if (c == null) {
					contactService.create(contact);
				}
				else {
					update(new ContactDto(contact));
				}
				
			} catch (MeveoApiException | BusinessException e) {
				failedToPersist.add(contact);
				failedToPersistLog.add(contact.toString() + " | " +  e.getMessage());
			}
		}
		
		ContactsDto contactsDto = new ContactsDto();
		for(Contact contact : failedToPersist) {
			contactsDto.getContact().add(new ContactDto(contact));
		}
		
		contactService.logContactError(failedToPersistLog);
		
		return contactsDto;
	}

	public void addTag(String code, String tag) throws BusinessException, EntityDoesNotExistsException {
		Contact contact = contactService.findByCode(code);
		if(contact != null) {
			if(!contact.getTags().contains(tag)) {
				contact.getTags().add(tag);
			}
			else throw new BusinessException("Contact code: " + code + " already has tag: " + tag);
		}
		else throw new EntityDoesNotExistsException(Contact.class, code, "code");
	}

	public void removeTag(String code, String tag) throws BusinessException, EntityDoesNotExistsException {
		Contact contact = contactService.findByCode(code);
		if(contact != null) {
			if(contact.getTags().contains(tag)) {
				contact.getTags().remove(tag);
			}
			else throw new BusinessException("Contact code: " + code + " do not contain tag: " + tag);
		}
		else throw new EntityDoesNotExistsException(Contact.class, code, "code");
	}

}
