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
package org.meveo.model.communication.contact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AccountEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ISearchable;
import org.meveo.model.communication.CommunicationPolicy;
import org.meveo.model.communication.Message;
import org.meveo.model.crm.ContactCustomer;
import org.meveo.model.crm.Customer;
import org.meveo.model.intcrm.AddressBook;

/**
 * Contact information
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ExportIdentifier({ "code" })
@Table(name = "com_contact")
@DiscriminatorValue(value = "ACCT_CTACT")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "com_contact_seq"), })
public class Contact extends AccountEntity implements ISearchable {

    private static final long serialVersionUID = 3772773449495155646L;

    public static final String ACCOUNT_TYPE = ((DiscriminatorValue) Contact.class.getAnnotation(DiscriminatorValue.class)).value();

    /**
     * Email address
     */
    @Column(name = "email", length = 255)
    @Size(max = 255)
    private String email;

    /**
     * Assistant's name
     */
    @Column(name = "assistant_name", length = 50)
    @Size(max = 50)
    private String assistantName;

    /**
     * Assistant's phone
     */
    @Column(name = "assistant_phone", length = 15)
    @Size(max = 15)
    private String assistantPhone;

    /**
     * Position
     */
    @Column(name = "position", length = 200)
    @Size(max = 200)
    private String position;

    /**
     * Company
     */
    @Column(name = "company", length = 200)
    @Size(max = 200)
    private String company;

    /**
     * Mobile phone number
     */
    @Column(name = "mobile", length = 15)
    @Size(max = 15)
    private String mobile;

    /**
     * Phone number
     */
    @Column(name = "phone", length = 15)
    @Size(max = 15)
    private String phone;

    /**
     * Website URL
     */
    @Column(name = "website_url", length = 255)
    @Size(max = 255)
    private String websiteUrl;

    /**
     * Where contact was imported form
     */
    @Column(name = "imported_from", length = 50)
    @Size(max = 50)
    private String importedFrom;

    /**
     * User that imported the contact
     */
    @Column(name = "imported_by", length = 50)
    @Size(max = 50)
    private String importedBy;

    /**
     * Social site identifier
     */
    @Column(name = "social_identifier", length = 2000)
    @Size(max = 2000)
    private String socialIdentifier;

    /**
     * Is it VIP contact
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_vip", columnDefinition = "tinyint default false")
    private boolean isVip;

    /**
     * Is it a prospect
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_prospect", columnDefinition = "tinyint default yes")
    private boolean isProspect;

    /**
     * Was user agreement accepted
     */
    @Type(type = "numeric_boolean")
    @Column(name = "agreed_ua", columnDefinition = "tinyint default false")
    private boolean agreedToUA;

    /**
     * Contact policy
     */
    @Embedded
    private CommunicationPolicy contactPolicy;

    /**
     * Messages send
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "contact", cascade = CascadeType.ALL)
    private List<Message> messages;

    /**
     * Address book
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_book_id")
    private AddressBook addressBook;

    /**
     * Tags
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "com_contact_tag", joinColumns = @JoinColumn(name = "contact_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<String>();

    @OneToMany( mappedBy = "contact",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactCustomer> customers = new ArrayList<>();

    public Contact() {
        accountType = ACCOUNT_TYPE;
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

    public String getSocialIdentifier() {
        return socialIdentifier;
    }

    public void setSocialIdentifier(String socialIdentifier) {
        this.socialIdentifier = socialIdentifier;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean isVip) {
        this.isVip = isVip;
    }

    public boolean isProspect() {
        return isProspect;
    }

    public void setProspect(boolean isProspect) {
        this.isProspect = isProspect;
    }

    public boolean isAgreedToUA() {
        return agreedToUA;
    }

    public void setAgreedToUA(boolean agreedToUA) {
        this.agreedToUA = agreedToUA;
    }

    public CommunicationPolicy getContactPolicy() {
        return contactPolicy;
    }

    public void setContactPolicy(CommunicationPolicy contactPolicy) {
        this.contactPolicy = contactPolicy;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public AddressBook getAddressBook() {
        return addressBook;
    }

    public void setAddressBook(AddressBook addressBook) {
        this.addressBook = addressBook;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String toString() {
        return this.getName().toString() + " code:" + this.getCode();
    }

    public List<ContactCustomer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<ContactCustomer> customers) {
        this.customers = customers;
    }

    public List<Customer> loadCustomers() {
        return customers.stream().map(c -> c.getCustomer()).collect(Collectors.toList());
    }

    public void addCustomers(List<Customer> customers) {
        customers.forEach(this::addCustomer);
    }

    public void addCustomer(Customer customer){
        ContactCustomer contactCustomer = new ContactCustomer(this, customer);
        this.customers.add(contactCustomer);
        customer.getContacts().add(contactCustomer);
    }
}