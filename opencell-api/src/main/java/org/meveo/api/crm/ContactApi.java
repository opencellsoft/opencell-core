/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.crm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.crm.AddressBookContactDto;
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.dto.crm.ContactsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.crm.ContactsResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.model.billing.Country;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.communication.contact.ContactCategory;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.intcrm.AddressBookContact;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.intcrm.impl.AdditionalDetailsService;
import org.meveo.service.intcrm.impl.AddressBookContactService;
import org.meveo.service.intcrm.impl.AddressBookService;
import org.meveo.service.intcrm.impl.ContactCategoryService;
import org.meveo.service.intcrm.impl.ContactService;

import com.opencsv.exceptions.CsvException;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
@Interceptors(SecuredBusinessEntityMethodInterceptor.class)
public class ContactApi extends BaseApi {

    @Inject
    ContactService contactService;

    @Inject
    private AddressBookContactService addressBookContactService;

    @Inject
    private TitleService titleService;

    @Inject
    private CountryService countryService;

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

    @Inject
    ContactCategoryService contactCategoryService;

    @TransactionAttribute
    public Contact create(ContactDto postData) throws MeveoApiException, BusinessException {

        if (postData.getName() == null) {
            missingParameters.add("name");
        } else {
            if (StringUtils.isBlank(postData.getName().getFirstName())) {
                missingParameters.add("firstName");
            }

            if (StringUtils.isBlank(postData.getName().getLastName())) {
                missingParameters.add("lastName");
            }
        }

        if ((postData.getContactInformation() == null || StringUtils.isBlank(postData.getContactInformation().getEmail())) && StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("email or code");
            // missingParameters.add("code");
        } else if (postData.getContactInformation() == null || StringUtils.isBlank(postData.getContactInformation().getEmail())) {
            missingParameters.add("email");
        }

        if(postData.getAddressBookContacts() == null || postData.getAddressBookContacts().isEmpty()){
            missingParameters.add("AddressBookContacts");
        }

        handleMissingParameters();

        if ((postData.getContactInformation() != null && !StringUtils.isBlank(postData.getContactInformation().getEmail()))){
            String email = postData.getContactInformation().getEmail();
            List<Contact> list = contactService.list(new PaginationConfiguration(Collections.singletonMap("contactInformation.email", email)));
            if(list.size() > 0){
                throw new EntityAlreadyExistsException(Contact.class, email, "email");
            }
        }

        String code = null;
        if ((postData.getContactInformation() != null && !StringUtils.isBlank(postData.getContactInformation().getEmail())) && StringUtils.isBlank(postData.getCode())) {
            code = postData.getContactInformation().getEmail();
        } else {
            code = postData.getCode();
        }

        Contact contact = contactService.findByCode(code);
        if (contact != null) {
            throw new EntityAlreadyExistsException(Contact.class, code, "code");
        }

        contact = new Contact();

        if ((postData.getContactInformation() != null && !StringUtils.isBlank(postData.getContactInformation().getEmail())) && StringUtils.isBlank(postData.getCode())) {
            postData.setCode(postData.getContactInformation().getEmail());
        }

        dtoToEntity(contact, postData);
        contactService.create(contact);
        linkToAddressBook(contact, postData.getAddressBookContacts());

        return contact;
    }

    private void linkToAddressBook(Contact contact, Set<AddressBookContactDto> addressBookContacts) {
        if(addressBookContacts != null && !addressBookContacts.isEmpty()){
            addressBookContacts.stream()
                    .filter(abcDto -> abcDto.getAddressBook() != null && abcDto.getAddressBook().containsKey("id"))
                    .forEach(abcDto -> {
                        AddressBook addressBookServiceById = addressBookService.findById(abcDto.getAddressBook().get("id"));
                        if(addressBookServiceById == null){
                            throw new EntityDoesNotExistsException("addressBook with id "+abcDto.getAddressBook().get("id")+" does not exist");
                        }
                        checkMainContactExistance(abcDto, addressBookServiceById);
                        AddressBookContact addressBookContact = new AddressBookContact(addressBookServiceById, contact, abcDto.getPosition(), abcDto.getMainContact());
                        addressBookContactService.create(addressBookContact);
                    });
        }
    }

    private void checkMainContactExistance(AddressBookContactDto abcDto, AddressBook addressBookServiceById) {
        if (abcDto.getMainContact() && addressBookContactService.hasMainContact(addressBookServiceById)) {
            throw new BusinessException("addressBook with id " + addressBookServiceById.getId() + " has already a main contact assigned.");
        }
    }
    private void checkMainContactExistance(AddressBookContactDto abcDto, AddressBook addressBookServiceById, Contact contact) {
        List<AddressBookContact> mainContact = addressBookContactService.getMainContact(addressBookServiceById.getId());
        if (abcDto.getMainContact() && !mainContact.isEmpty() && !isTheSameAddressBook(mainContact, contact)) {
            throw new BusinessException("addressBook with id " + addressBookServiceById.getId() + " has already a main contact assigned.");
        }
    }

    private boolean isTheSameAddressBook(List<AddressBookContact> mainContact, Contact contact) {
        return mainContact.stream()
                .filter(x -> x.getMainContact())
                .anyMatch(x -> x.getContact().getId() == contact.getId());
    }

    public Contact update(ContactDto postData) throws MeveoApiException, BusinessException {

        if ((postData.getContactInformation() == null || StringUtils.isBlank(postData.getContactInformation().getEmail())) && StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("email or code");
        }
        
        handleMissingParameters();

        String code = null;
        if ((postData.getContactInformation() != null && !StringUtils.isBlank(postData.getContactInformation().getEmail())) && StringUtils.isBlank(postData.getCode())) {
            code = postData.getContactInformation().getEmail();
        } else {
            code = postData.getCode();
        }

        Contact contact = contactService.findByCode(code);
        if (contact == null) {
            throw new EntityDoesNotExistsException(Contact.class, code, "code");
        }

        dtoToEntity(contact, postData);
        updateContactAddressBook(contact, postData.getAddressBookContacts());
        contact = contactService.update(contact);
        return contact;
    }

    private void updateContactAddressBook(Contact contact, Set<AddressBookContactDto> addressBookContacts) {
        if(addressBookContacts != null && !addressBookContacts.isEmpty()){
        	contact.getAddressBookContacts().clear();
            addressBookContacts.stream()
                    .forEach(abcDto -> {
                        if(abcDto.getAddressBook() == null || abcDto.getAddressBook().get("id") == null){
                           throw new BusinessException("addressBook contact id is required to assigne contact to an address book");
                        }
                        AddressBook addressBookServiceById = addressBookService.findById(abcDto.getAddressBook().get("id"));
                        if(addressBookServiceById == null){
                            throw new EntityDoesNotExistsException("addressBook with id "+abcDto.getAddressBook().get("id")+" does not exist");
                        }
                        checkMainContactExistance(abcDto, addressBookServiceById, contact);
                        
                        AddressBookContact addressBookContact = new AddressBookContact(addressBookServiceById, contact, abcDto.getPosition(), abcDto.getMainContact());
                        addressBookContactService.create(addressBookContact);
                        
                    });
        }
    }

    /**
     * Populate entity with fields from DTO entity
     *
     * @param contact Entity to populate
     * @param postData DTO entity object to populate from
     **/
    private void dtoToEntity(Contact contact, ContactDto postData) {

        boolean isNew = contact.getId() == null;

        updateAccount(contact, postData, true);

        if (postData.getAssistantName() != null) {
            contact.setAssistantName(StringUtils.isBlank(postData.getAssistantName()) ? null : postData.getAssistantName());
        }
        if (postData.getAssistantPhone() != null) {
            contact.setAssistantPhone(StringUtils.isBlank(postData.getAssistantPhone()) ? null : postData.getAssistantPhone());
        }
        if (postData.getPosition() != null) {
            contact.setPosition(StringUtils.isBlank(postData.getPosition()) ? null : postData.getPosition());
        }
        if (postData.getSocialIdentifier() != null) {
            contact.setSocialIdentifier(StringUtils.isBlank(postData.getSocialIdentifier()) ? null : postData.getSocialIdentifier());
        }
        if (postData.getWebsiteUrl() != null) {
            contact.setWebsiteUrl(StringUtils.isBlank(postData.getWebsiteUrl()) ? null : postData.getWebsiteUrl());
        }
        if (postData.isVip() != null) {
            contact.setVip(postData.isVip());
        }
        if (postData.isProspect() != null) {
            contact.setProspect(postData.isProspect());
        }
        if (postData.isAgreedToUA() != null) {
            contact.setAgreedToUA(postData.isAgreedToUA());
        }

        if (postData.getTags() != null) {
            contact.setTags(postData.getTags());
        }

        if (postData.getReference() != null) {
            contact.setReference(postData.getReference());
        }

        if (postData.getComment() != null) {
            contact.setComment(postData.getComment());
        }

        if (isNew || (contact.getCompany() != null && contact.getCompany().equals(postData.getCompany()))) {
            contact.setCompany(postData.getCompany());
            Customer customer = null;
            if (contact.getCompany() == null || contact.getCompany().isEmpty()) {
                customer = customerService.findByCompanyName("UNASSIGNED");
                if (customer == null)
                    customer = contactService.createUnassignedCustomer();
                contact.setAddressBook(customer.getAddressbook());
            } else {
                customer = customerService.findByCompanyName(contact.getCompany());
                if (customer != null) {
                    contact.setAddressBook(customer.getAddressbook());
                } else {
                    customer = contactService.createCustomerFromContact(contact);
                }
            }
        }

        if(postData.getContactCategoryCodes() != null) {
        	contact.getContactCategories().clear();
        	postData.getContactCategoryCodes().forEach(ccCode -> {
        		var cc = contactCategoryService.findByCode(ccCode);
        		if(cc == null) {
        			throw new EntityDoesNotExistsException(ContactCategory.class, ccCode);
        		}
        		contact.getContactCategories().add(cc);
        	});
        }
    }

    public void updateAccount(Contact contact, ContactDto postData, boolean checkCustomFields) throws MeveoApiException {

        boolean isNew = contact.getId() == null;
        if (isNew) {
            contact.setCode(postData.getCode());
        } else if (!StringUtils.isBlank(postData.getUpdatedCode())) {
            contact.setCode(postData.getUpdatedCode());
        }

        if (postData.getAddress() != null) {
            Address address = contact.getAddress() == null ? new Address() : contact.getAddress();

            if (postData.getAddress().getAddress1() != null) {
                address.setAddress1(StringUtils.isEmpty(postData.getAddress().getAddress1()) ? null : postData.getAddress().getAddress1());
            }
            if (postData.getAddress().getAddress2() != null) {
                address.setAddress2(StringUtils.isEmpty(postData.getAddress().getAddress2()) ? null : postData.getAddress().getAddress2());
            }
            if (postData.getAddress().getAddress3() != null) {
                address.setAddress3(StringUtils.isEmpty(postData.getAddress().getAddress3()) ? null : postData.getAddress().getAddress3());
            }
            if (postData.getAddress().getAddress4() != null) {
                address.setAddress4(StringUtils.isEmpty(postData.getAddress().getAddress4()) ? null : postData.getAddress().getAddress4());
            }
            if (postData.getAddress().getAddress5() != null) {
                address.setAddress5(StringUtils.isEmpty(postData.getAddress().getAddress5()) ? null : postData.getAddress().getAddress5());
            }
            if (postData.getAddress().getZipCode() != null) {
                address.setZipCode(StringUtils.isEmpty(postData.getAddress().getZipCode()) ? null : postData.getAddress().getZipCode());
            }
            if (postData.getAddress().getCity() != null) {
                address.setCity(StringUtils.isEmpty(postData.getAddress().getCity()) ? null : postData.getAddress().getCity());
            }

            if (postData.getAddress().getCountry() != null) {
                if (StringUtils.isBlank(postData.getAddress().getCountry())) {
                    address.setCountry(null);
                } else {
                    Country country = countryService.findByCode(postData.getAddress().getCountry());
                    if (country == null) {
                        throw new EntityDoesNotExistsException(Country.class, postData.getAddress().getCountry());
                    } else {
                        address.setCountry(country);
                    }
                }
            }

            if (postData.getAddress().getState() != null) {
                address.setState(StringUtils.isEmpty(postData.getAddress().getState()) ? null : postData.getAddress().getState());
            }

            contact.setAddress(address);
        }

        if (postData.getName() != null) {

            // All name attributes are empty - remove name field alltogether
            if ((postData.getName().getTitle() != null && StringUtils.isEmpty(postData.getName().getTitle())) && (postData.getName().getFirstName() != null && StringUtils.isEmpty(postData.getName().getFirstName()))
                    && (postData.getName().getLastName() != null && StringUtils.isEmpty(postData.getName().getLastName()))) {
                contact.setName(null);

            } else {
                Name name = contact.getName() == null ? new Name() : contact.getName();

                if (postData.getName().getFirstName() != null) {
                    name.setFirstName(StringUtils.isEmpty(postData.getName().getFirstName()) ? null : postData.getName().getFirstName());
                }
                if (postData.getName().getLastName() != null) {
                    name.setLastName(StringUtils.isEmpty(postData.getName().getLastName()) ? null : postData.getName().getLastName());
                }
                if (postData.getName().getTitle() != null) {
                    if (StringUtils.isBlank(postData.getName().getTitle())) {
                        name.setTitle(null);
                    } else {
                        Title title = titleService.findByCode(postData.getName().getTitle());
                        if (title == null) {
                            throw new EntityDoesNotExistsException(Title.class, postData.getName().getTitle());
                        } else {
                            name.setTitle(title);
                        }
                    }
                }

                contact.setName(name);
            }
        }

        if (postData.getDescription() != null) {
            contact.setDescription(StringUtils.isEmpty(postData.getDescription()) ? null : postData.getDescription());
        }
        if (postData.getExternalRef1() != null) {
            contact.setExternalRef1(StringUtils.isEmpty(postData.getExternalRef1()) ? null : postData.getExternalRef1());
        }
        if (postData.getExternalRef2() != null) {
            contact.setExternalRef2(StringUtils.isEmpty(postData.getExternalRef2()) ? null : postData.getExternalRef2());
        }
        if (postData.getJobTitle() != null) {
            contact.setJobTitle(StringUtils.isEmpty(postData.getJobTitle()) ? null : postData.getJobTitle());
        }
        if (postData.getVatNo() != null) {
            contact.setVatNo(StringUtils.isEmpty(postData.getVatNo()) ? null : postData.getVatNo());
        }
        if (postData.getRegistrationNo() != null) {
            contact.setRegistrationNo(StringUtils.isEmpty(postData.getRegistrationNo()) ? null : postData.getRegistrationNo());
        }

        if (postData.getContactInformation() != null) {
            if (contact.getContactInformation() == null) {
                contact.setContactInformation(new ContactInformation());
            }
            if (postData.getContactInformation().getEmail() != null) {
                contact.getContactInformation().setEmail(StringUtils.isEmpty(postData.getContactInformation().getEmail()) ? null : postData.getContactInformation().getEmail());
            }
            if (postData.getContactInformation().getPhone() != null) {
                contact.getContactInformation().setPhone(StringUtils.isEmpty(postData.getContactInformation().getPhone()) ? null : postData.getContactInformation().getPhone());
            }
            if (postData.getContactInformation().getMobile() != null) {
                contact.getContactInformation().setMobile(StringUtils.isEmpty(postData.getContactInformation().getMobile()) ? null : postData.getContactInformation().getMobile());
            }
            if (!StringUtils.isBlank(postData.getContactInformation().getFax())) {
                contact.getContactInformation().setFax(postData.getContactInformation().getFax());
            }
        }

    }

    public Contact createOrUpdate(ContactDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getName().getFirstName())) {
            missingParameters.add("firstName");
        }

        if (StringUtils.isBlank(postData.getName().getLastName())) {
            missingParameters.add("lastName");
        }

        if ((postData.getContactInformation() == null || StringUtils.isBlank(postData.getContactInformation().getEmail())) && StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("email or code");
            // missingParameters.add("code");
        }
        
        handleMissingParameters();

        String code = null;
        if ((postData.getContactInformation() != null && !StringUtils.isBlank(postData.getContactInformation().getEmail())) && StringUtils.isBlank(postData.getCode())) {
            code = postData.getContactInformation().getEmail();
        } else {
            code = postData.getCode();
        }

        Contact contact = contactService.findByCode(code);

        if (contact == null) {
        	if(postData.getAddressBookContacts() == null || postData.getAddressBookContacts().isEmpty()){
                missingParameters.add("AddressBookContacts");
            }
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
        Contact contact = contactService.findByCode(code);

        if (contact == null) {
            throw new EntityDoesNotExistsException(Contact.class, code, "code");
        }
        return buildContactDto(contact);
    }

    private ContactDto buildContactDto(Contact contact) {
        ContactDto contactDto;
        List<AddressBookContact> addressBookContactServiceByContact = addressBookContactService.findByContact(contact);
        if(addressBookContactServiceByContact != null){
            Map<AddressBookContact, Customer> addressBookContactCustomers = new HashMap<>();
            addressBookContactServiceByContact
                    .stream()
                    .filter(abc -> abc.getAddressBook() != null)
                    .forEach(abc -> addressBookContactCustomers.put(abc, customerService.findByAddressBook(abc.getAddressBook().getId())));
            contactDto = new ContactDto(contact, addressBookContactCustomers);
        } else {
            contactDto = new ContactDto(contact);
        }
        return contactDto;
    }

    public ContactsResponseDto listGetAll(PagingAndFiltering pagingAndFiltering) {
        ContactsResponseDto result = new ContactsResponseDto();
        result.setPaging(pagingAndFiltering);

        List<Contact> contacts = contactService.list(GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration());
        if (contacts != null) {
            result.getContacts().setContact(contacts.stream()
                            .map(contact -> buildContactDto(contact))
                    .collect(Collectors.toList()));
            result.getContacts().setTotalNumberOfRecords(Long.valueOf(result.getContacts().getContact().size()));
        }

        return result;
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "contacts.contact", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = Contact.class) }, totalRecords = "contacts.totalNumberOfRecords")
    public ContactsResponseDto list(ContactDto postData, PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
        return list(postData, pagingAndFiltering, CustomFieldInheritanceEnum.INHERIT_NO_MERGE);
    }

    @SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "contacts.contact", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = Contact.class) }, totalRecords = "contacts.totalNumberOfRecords")
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
                contactsDto.getContact().add(buildContactDto(c));
            }
        }
        result.setContacts(contactsDto);
        return result;
    }

    public ContactsDto importCSVText(String context) throws IOException, CsvException {
        List<Contact> failedToPersist = new ArrayList<Contact>();
        List<Contact> contacts = null;
        List<String> failedToPersistLog = new ArrayList<String>();

        contacts = contactService.parseCSVText(context);

        for (Contact contact : contacts) {
            if (StringUtils.isBlank(contact.getName().getFirstName())) {
                missingParameters.add("firstName");
            }
            if (StringUtils.isBlank(contact.getName().getLastName())) {
                missingParameters.add("lastName");
            }

            if ((contact.getContactInformation() == null || StringUtils.isBlank(contact.getContactInformation().getEmail())) && StringUtils.isBlank(contact.getCode())) {
                missingParameters.add("email");
                missingParameters.add("code");
            }

            try {
                handleMissingParameters();

                Contact c = contactService.findByCode(contact.getCode());
                if (c == null) {
                    contactService.create(contact);
                } else {
                    update(new ContactDto(contact));
                }

            } catch (MeveoApiException | BusinessException e) {
                failedToPersist.add(contact);
                failedToPersistLog.add(contact.toString() + " | " + e.getMessage());
            }
        }

        ContactsDto contactsDto = new ContactsDto();
        for (Contact contact : failedToPersist) {
            contactsDto.getContact().add(new ContactDto(contact));
        }

        contactService.logContactError(failedToPersistLog);

        return contactsDto;
    }

    public void addTag(String code, String tag) throws BusinessException, EntityDoesNotExistsException {
        Contact contact = contactService.findByCode(code);
        if (contact != null) {
            if (!contact.getTags().contains(tag)) {
                contact.getTags().add(tag);
            } else
                throw new BusinessException("Contact code: " + code + " already has tag: " + tag);
        } else
            throw new EntityDoesNotExistsException(Contact.class, code, "code");
    }

    public void removeTag(String code, String tag) throws BusinessException, EntityDoesNotExistsException {
        Contact contact = contactService.findByCode(code);
        if (contact != null) {
            if (contact.getTags().contains(tag)) {
                contact.getTags().remove(tag);
            } else
                throw new BusinessException("Contact code: " + code + " do not contain tag: " + tag);
        } else
            throw new EntityDoesNotExistsException(Contact.class, code, "code");
    }

}
