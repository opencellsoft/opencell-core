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

package org.meveo.api.dto.crm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.meveo.api.dto.account.AccountDto;
import org.meveo.model.communication.CommunicationPolicy;
import org.meveo.model.communication.contact.Contact;

public class ContactDto extends AccountDto {

    private static final long serialVersionUID = -7359294810585172609L;

    private String email;

    private String assistantName;

    private String assistantPhone;

    private String position;

    private String company;

    private String mobile;

    private String phone;

    private String websiteUrl;

    private String importedFrom;

    private String importedBy;

    private String socialIdentifier;

    private Boolean isVip;

    private Boolean isProspect;

    private Boolean agreedToUA;

    private CommunicationPolicy contactPolicy;

//	private List<Message> messages;

    private Set<String> tags = new HashSet<String>();

    private List<CustomerContactDto> customersContact;

    public ContactDto() {

    }

    public ContactDto(Contact contact) {
        super(contact, null);
        email = contact.getEmail();
        assistantName = contact.getAssistantName();
        assistantPhone = contact.getAssistantPhone();
        position = contact.getPosition();
        company = contact.getCompany();
        phone = contact.getPhone();
        mobile = contact.getMobile();
        websiteUrl = contact.getWebsiteUrl();
        importedBy = contact.getImportedBy();
        importedFrom = contact.getImportedFrom();
        socialIdentifier = contact.getSocialIdentifier();
        isVip = contact.isVip();
        isProspect = contact.isProspect();
        agreedToUA = contact.isAgreedToUA();
        tags = contact.getTags();
        customersContact = contact.getCustomers().stream()
                .map(customer -> {
                    CustomerContactDto customerContactDto = new CustomerContactDto();
                    customerContactDto.setContactCode(contact.getCode());
                    customerContactDto.setCustomerCode(customer.getCustomer().getCode());
                    customerContactDto.setRole(customer.getRole());
                    return customerContactDto;
                })
                .collect(Collectors.toList());
//		messages = contact.getMessages();

//		addressBook = new AddressBookDto(contact.getAddressBook());

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAssistantName() {
        return assistantName;
    }

    public void setAssistantName(String assistantName) {
        this.assistantName = assistantName;
    }

    public String getAssistantPhone() {
        return assistantPhone;
    }

    public void setAssistantPhone(String assistantPhone) {
        this.assistantPhone = assistantPhone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getImportedFrom() {
        return importedFrom;
    }

    public void setImportedFrom(String importedFrom) {
        this.importedFrom = importedFrom;
    }

    public String getImportedBy() {
        return importedBy;
    }

    public void setImportedBy(String importedBy) {
        this.importedBy = importedBy;
    }

    public Boolean isVip() {
        return isVip;
    }

    public void setVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public Boolean isProspect() {
        return isProspect;
    }

    public void setProspect(Boolean isProspect) {
        this.isProspect = isProspect;
    }

    public Boolean isAgreedToUA() {
        return agreedToUA;
    }

    public void setAgreedToUA(Boolean agreedToUA) {
        this.agreedToUA = agreedToUA;
    }

    public CommunicationPolicy getContactPolicy() {
        return contactPolicy;
    }

    public void setContactPolicy(CommunicationPolicy contactPolicy) {
        this.contactPolicy = contactPolicy;
    }

//	public List<Message> getMessages() {
//		return messages;
//	}
//
//	public void setMessages(List<Message> messages) {
//		this.messages = messages;
//	}

    public String getSocialIdentifier() {
        return socialIdentifier;
    }

    public void setSocialIdentifier(String socialIdentifier) {
        this.socialIdentifier = socialIdentifier;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public List<CustomerContactDto> getCustomersContact() {
        return customersContact;
    }

    public void setCustomersContact(List<CustomerContactDto> customersContact) {
        this.customersContact = customersContact;
    }
}
