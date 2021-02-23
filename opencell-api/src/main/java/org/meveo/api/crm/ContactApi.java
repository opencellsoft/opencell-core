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
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.elasticsearch.common.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.account.AccountEntityApi;
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.dto.crm.ContactsDto;
import org.meveo.api.dto.crm.CustomerContactDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.crm.ContactsResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.security.Interceptor.SecuredBusinessEntityMethodInterceptor;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.catalog.impl.TitleService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerContactService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.intcrm.impl.AdditionalDetailsService;
import org.meveo.service.intcrm.impl.AddressBookService;
import org.meveo.service.intcrm.impl.ContactService;
import org.primefaces.model.SortOrder;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
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

    @Inject
    CustomerContactService customerContactService;

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

        if (StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("email");
            // missingParameters.add("code");
        } else if (StringUtils.isBlank(postData.getEmail())) {
            missingParameters.add("email");
        }

        handleMissingParameters();

        String code = null;
        if (!StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {
            code = postData.getEmail();
        } else {
            code = postData.getCode();
        }

        Contact contact = contactService.findByCode(code);
        if (contact != null) {
            throw new EntityAlreadyExistsException(Contact.class, code, "code");
        }
        
        contact = new Contact();

        if (!StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {
            postData.setCode(postData.getEmail());
            postData.setEmail(postData.getEmail());
        }

        dtoToEntity(contact, postData);

        contactService.create(contact);

        if(postData.getCustomersContact() != null){
            addCustomers(contact, postData.getCustomersContact());
        }

        return contact;
    }

    private void addCustomers(Contact contact, List<CustomerContactDto> customerContacts) {
        for (CustomerContactDto customerContactCDto: customerContacts) {
            if(Strings.isEmpty(customerContactCDto.getCustomerCode()))
                missingParameters.add("customerCode");
            handleMissingParameters();

            Customer customer = customerService.findByCode(customerContactCDto.getCustomerCode());
            if (customer == null)
                throw new EntityDoesNotExistsException(Customer.class, customerContactCDto.getCustomerCode());
            customerContactService.create(contact, customer, customerContactCDto.getRole());
        }
    }

    public Contact update(ContactDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("email or code");
        }

        handleMissingParameters();

        String code = null;
        if (!StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {
            code = postData.getEmail();
        } else {
            code = postData.getCode();
        }

        Contact contact = contactService.findByCode(code);
        if (contact == null) {
            throw new EntityDoesNotExistsException(Contact.class, code, "code");
        }

        dtoToEntity(contact, postData);
        if(postData.getCustomersContact() != null){
            customerContactService.removeAll(contact.getCustomers());
            contact.getCustomers().clear();
            addCustomers(contact, postData.getCustomersContact());
        }

        contact = contactService.update(contact);
        return contact;
    }

    /**
     * Populate entity with fields from DTO entity
     * 
     * @param contact Entity to populate
     * @param postData DTO entity object to populate from
     **/
    private void dtoToEntity(Contact contact, ContactDto postData) {

        boolean isNew = contact.getId() == null;

        updateAccount(contact, postData);

        if (postData.getEmail() != null) {
            contact.setEmail(StringUtils.isBlank(postData.getEmail()) ? null : postData.getEmail());
        }
        if (postData.getMobile() != null) {
            contact.setMobile(StringUtils.isBlank(postData.getMobile()) ? null : postData.getMobile());
        }
        if (postData.getPhone() != null) {
            contact.setPhone(StringUtils.isBlank(postData.getPhone()) ? null : postData.getPhone());
        }
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
            // missingParameters.add("code");
        }

        handleMissingParameters();

        String code = null;
        if (!StringUtils.isBlank(postData.getEmail()) && StringUtils.isBlank(postData.getCode())) {
            code = postData.getEmail();
        } else {
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

        for (Contact contact : contacts) {
            if (StringUtils.isBlank(contact.getName().getFirstName())) {
                missingParameters.add("firstName");
            }
            if (StringUtils.isBlank(contact.getName().getLastName())) {
                missingParameters.add("lastName");
            }
            if (StringUtils.isBlank(contact.getEmail()) && StringUtils.isBlank(contact.getCode())) {
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

    public ContactDto addCustomers(String contactCode, List<CustomerContactDto> customerContactDtos){
        Contact contact = contactService.findByCode(contactCode);
        if(contact == null)
            throw new EntityDoesNotExistsException(Contact.class, contactCode);
        addCustomers(contact, customerContactDtos);
        contact = contactService.update(contact);
        return new ContactDto(contact);
    }

}
