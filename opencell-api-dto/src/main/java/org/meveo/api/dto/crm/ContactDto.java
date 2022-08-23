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
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.AuditableDto;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.account.AccountDto;
import org.meveo.api.dto.account.AddressDto;
import org.meveo.api.dto.account.ContactInformationDto;
import org.meveo.api.dto.account.NameDto;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.model.communication.CommunicationPolicy;
import org.meveo.model.communication.contact.Contact;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public class ContactDto extends BusinessEntityDto {

    private static final long serialVersionUID = -7359294810585172609L;

    /** The external ref 1. */
    @Schema(description = "The external ref 1")
    private String externalRef1;

    /** The external ref 2. */
    @Schema(description = "The external ref 2")
    private String externalRef2;

    /** The name. */
    @Schema(description = "The name")
    private NameDto name;

    /** The address. */
    @Schema(description = "The address related to account")
    private AddressDto address;

    /** The job title. */
    @Schema(description = "The job title")
    private String jobTitle;

    /** The custom fields. */
    @Schema(description = "The custom fields")
    private CustomFieldsDto customFields;

    /** The loaded. */
    @XmlTransient
    protected boolean loaded = false;

    /** The vat no. */
    @Schema(description = "The vat no")
    private String vatNo;

    /** The registration no. */
    @Schema(description = "The registration no")
    private String registrationNo;

    /** The contact information. */
    @Schema(description = "The contact information")
    private ContactInformationDto contactInformation;

    @Schema(description = "indicate if this is a company")
    protected Boolean isCompany = Boolean.FALSE;

    @Schema(description = "The legal entity type")
    protected TitleDto legalEntityType;

    private String assistantName;

    private String assistantPhone;

    private String position;

    private String company;

    private String websiteUrl;

    private String importedFrom;

    private String importedBy;

    private String socialIdentifier;

    private String reference;

    private String comment;

    @JsonProperty("isVip")
    @JsonAlias({ "isVip", "vip" })
    private boolean isVip;

    @JsonProperty("isProspect")
    @JsonAlias({ "isProspect", "prospect" })
    private boolean isProspect;

    private Boolean agreedToUA;

    private CommunicationPolicy contactPolicy;

    @JsonIgnore
    private AuditableDto auditableNullSafe;

//	private List<Message> messages;

    private Set<String> tags = new HashSet<String>();

    public ContactDto() {

    }

    public ContactDto(Contact contact) {
        super(contact);
        setAuditableEntity(contact);
        setExternalRef1(contact.getExternalRef1());
        setExternalRef2(contact.getExternalRef2());
        if (contact.getName() != null) {
            setName(new NameDto(contact.getName()));
        }
        if (contact.getAddress() != null) {
            setAddress(new AddressDto(contact.getAddress()));
        }
        if (contact.getContactInformation() != null) {
            setContactInformation(new ContactInformationDto(contact.getContactInformation()));
        }

        assistantName = contact.getAssistantName();
        assistantPhone = contact.getAssistantPhone();
        position = contact.getPosition();
        company = contact.getCompany();
        websiteUrl = contact.getWebsiteUrl();
        importedBy = contact.getImportedBy();
        importedFrom = contact.getImportedFrom();
        socialIdentifier = contact.getSocialIdentifier();
        isVip = contact.isVip();
        isProspect = contact.isProspect();
        agreedToUA = contact.isAgreedToUA();
        comment = contact.getComment();
        if (contact.getTags() != null && !contact.getTags().isEmpty()) {
            tags = contact.getTags();
        }
        
        // Return Job Title
        jobTitle = contact.getJobTitle();
//		messages = contact.getMessages();

//		addressBook = new AddressBookDto(contact.getAddressBook());

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

    @Override
    public String toString() {
        return "ContactDto [email=" + (getContactInformation() != null ? getContactInformation().getEmail() : null) + ", assistantName=" + assistantName + ", assistantPhone=" + assistantPhone + ", company=" + company
                + ", phone=" + (getContactInformation() != null ? getContactInformation().getPhone() : null) + ", websiteUrl=" + websiteUrl + ", socialIdentifier=" + socialIdentifier + "]";
    }

    /**
     * Gets the external ref 1.
     *
     * @return the external ref 1
     */
    public String getExternalRef1() {
        return externalRef1;
    }

    /**
     * Sets the external ref 1.
     *
     * @param externalRef1 the new external ref 1
     */
    public void setExternalRef1(String externalRef1) {
        this.externalRef1 = externalRef1;
    }

    /**
     * Gets the external ref 2.
     *
     * @return the external ref 2
     */
    public String getExternalRef2() {
        return externalRef2;
    }

    /**
     * Sets the external ref 2.
     *
     * @param externalRef2 the new external ref 2
     */
    public void setExternalRef2(String externalRef2) {
        this.externalRef2 = externalRef2;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public NameDto getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(NameDto name) {
        this.name = name;
    }

    /**
     * Gets the address.
     *
     * @return the address
     */
    public AddressDto getAddress() {
        return address;
    }

    /**
     * Sets the address.
     *
     * @param address the new address
     */
    public void setAddress(AddressDto address) {
        this.address = address;
    }


    /**
     * Gets the custom fields.
     *
     * @return the custom fields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the new custom fields
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Checks if is loaded.
     *
     * @return true, if is loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Sets the loaded.
     *
     * @param loaded the new loaded
     */
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccountDto other = (AccountDto) obj;
        if (getCode() == null) {
            if (other.getCode() != null)
                return false;
        } else if (!getCode().equals(other.getCode()))
            return false;
        return true;
    }

    /**
     * Gets the job title.
     *
     * @return the job title
     */
    public String getJobTitle() {
        return jobTitle;
    }

    /**
     * Sets the job title.
     *
     * @param jobTitle the new job title
     */
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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

    public ContactInformationDto getContactInformation() {
        if (contactInformation == null) {
            contactInformation = new ContactInformationDto();
        }
        return contactInformation;
    }

    public void setContactInformation(ContactInformationDto contactInformation) {
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

    /**
     * @return the legalEntityType
     */
    public TitleDto getLegalEntityType() {
        return legalEntityType;
    }

    /**
     * @param legalEntityType the legalEntityType to set
     */
    public void setLegalEntityType(TitleDto legalEntityType) {
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
}