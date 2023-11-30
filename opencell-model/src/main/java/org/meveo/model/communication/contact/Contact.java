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

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ISearchable;
import org.meveo.model.communication.CommunicationPolicy;
import org.meveo.model.communication.Message;
import org.meveo.model.crm.ProviderContact;
import org.meveo.model.intcrm.AddressBook;
import org.meveo.model.intcrm.AddressBookContact;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;

/**
 * Contact information
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ExportIdentifier({ "code" })
@Table(name = "com_contact")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "com_contact_seq"), })
public class Contact extends BusinessCFEntity implements ISearchable {

    private static final long serialVersionUID = 3772773449495155646L;

    /**
     * External reference 1
     */
    @Column(name = "external_ref_1", length = 255)
    @Size(max = 255)
    protected String externalRef1;

    /**
     * External reference 2
     */
    @Column(name = "external_ref_2", length = 255)
    @Size(max = 255)
    protected String externalRef2;

    /**
     * Account name information
     */
    @Embedded
    protected Name name;

    /**
     * Account address information
     */
    @Embedded
    protected Address address;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Type(type = "numeric_boolean")
    @Column(name = "default_level")
    protected Boolean defaultLevel = true;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "provider_contact", length = 255)
    @Size(max = 255)
    protected String providerContact;

    /**
     * Primary contact
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_contact")
    protected ProviderContact primaryContact;

    /**
     * Job title
     */
    @Column(name = "job_title", length = 255)
    private String jobTitle;

    /**
     * Contact information
     */
    @Embedded
    private ContactInformation contactInformation;

    /**
     * VAT number
     */
    @Column(name = "vat_no", length = 100)
    private String vatNo;

    /**
     * Registration number
     */
    @Column(name = "registration_no", length = 100)
    private String registrationNo;

    @Column(name = "company")
    @Type(type = "numeric_boolean")
    protected Boolean isCompany = Boolean.FALSE;

    @Column(name = "entreprise")
    @Type(type = "numeric_boolean")
    protected Boolean isEnterprise = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_entity_type_id")
    protected Title legalEntityType;

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
     * deprecated, please use the AddressBookContact position field instead
     */
    @Deprecated
    @Column(name = "position", length = 200)
    @Size(max = 200)
    private String position;

    /**
     * Company
     */
    @Column(name = "company_name", length = 200)
    @Size(max = 200)
    private String company;

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
    @Column(name = "is_vip")
    private boolean isVip;

    /**
     * Is it a prospect
     */
    @Type(type = "numeric_boolean")
    @Column(name = "is_prospect")
    private boolean isProspect;

    /**
     * Was user agreement accepted
     */
    @Type(type = "numeric_boolean")
    @Column(name = "agreed_ua")
    private boolean agreedToUA;

    /**
     * Contact policy
     */
    @Embedded
    private CommunicationPolicy contactPolicy;

    /**
     * Messages send
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contact", cascade = CascadeType.ALL)
    private List<Message> messages;

    /**
     * Address book
     */
    @Deprecated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_book_id")
    private AddressBook addressBook;

    /**
     * Tags
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "com_contact_tag", joinColumns = @JoinColumn(name = "contact_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<String>();

    @Column(name = "reference", length = 80)
    @Size(max = 80)
    private String reference;


    @Column(name = "comment", length = 2000)
    @Size(max = 2000)
    private String comment;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contact", cascade = CascadeType.ALL)
    private List<AddressBookContact> addressBookContacts;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "com_contact_category_contact", joinColumns = @JoinColumn(name = "contact_id"), inverseJoinColumns = @JoinColumn(name = "contact_category_id"))
    private List<ContactCategory> contactCategories = new ArrayList<>(); 

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

    public String getExternalRef1() {
        return externalRef1;
    }

    public void setExternalRef1(String externalRef1) {
        this.externalRef1 = externalRef1;
    }

    public String getExternalRef2() {
        return externalRef2;
    }

    public void setExternalRef2(String externalRef2) {
        this.externalRef2 = externalRef2;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ProviderContact getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(ProviderContact primaryContact) {
        this.primaryContact = primaryContact;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void anonymize(String code) {
        if (name != null) {
            name.anonymize(code);
        }
        if (address != null) {
            address.anonymize(code);
        }
        getContactInformationNullSafe().anonymize(code);
    }

    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    /**
     * Instantiate contactInformation field if it is null. NOTE: do not use this method unless you have an intention to modify it's value, as entity will be marked dirty and record will be updated in DB
     * 
     * @return ContactInformation value or instantiated ContactInformation field value
     */
    public ContactInformation getContactInformationNullSafe() {
        if (contactInformation == null) {
            contactInformation = new ContactInformation();
        }
        return contactInformation;
    }

    public ContactInformation getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(ContactInformation contactInformation) {
        this.contactInformation = contactInformation;
    }

    /**
     * @return the isCompany
     */
    public Boolean getIsCompany() {
        return isCompany;
    }

    /**
     * @param isCompany the isCompany to set
     */
    public void setIsCompany(Boolean isCompany) {
        this.isCompany = isCompany;
    }


    public Boolean getEnterprise() {
        return isEnterprise;
    }

    public void setEnterprise(Boolean enterprise) {
        isEnterprise = enterprise;
    }

    /**
     * @return the legalEntityType
     */
    public Title getLegalEntityType() {
        return legalEntityType;
    }

    /**
     * @param legalEntityType the legalEntityType to set
     */
    public void setLegalEntityType(Title legalEntityType) {
        this.legalEntityType = legalEntityType;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<AddressBookContact> getAddressBookContacts() {
        return addressBookContacts;
    }

    public void setAddressBookContacts(List<AddressBookContact> addressBookContacts) {
        this.addressBookContacts = addressBookContacts;
    }

	public List<ContactCategory> getContactCategories() {
		return contactCategories;
	}

	public void setContactCategories(List<ContactCategory> contactCategories) {
		this.contactCategories = contactCategories;
	}

}