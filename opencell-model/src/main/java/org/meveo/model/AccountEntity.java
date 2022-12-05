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
package org.meveo.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.ProviderContact;
import org.meveo.model.listeners.AccountCodeGenerationListener;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

/**
 * Parent class of all account entities
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Entity
@ObservableEntity
@Table(name = "account_entity", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "account_entity_seq"), })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EntityListeners({ AccountCodeGenerationListener.class })
public abstract class AccountEntity extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

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
    @Convert(converter = NumericBooleanConverter.class)
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
     * Business account model that created this account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bam_id")
    protected BusinessAccountModel businessAccountModel;

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

    /**
     * Expression to determine minimum amount value
     */
    @Column(name = "minimum_amount_el", length = 2000)
    @Size(max = 2000)
    private String minimumAmountEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    @Column(name = "minimum_label_el", length = 2000)
    @Size(max = 2000)
    private String minimumLabelEl;

    /** Corresponding to minimum one shot charge template */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minimum_charge_template_id")
    private OneShotChargeTemplate minimumChargeTemplate;
    
    /**
     * Corresponding to minimum invoice AccountingArticle
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minimum_article_id")
    private AccountingArticle minimumArticle;
    
    @Column(name = "company")
    @Convert(converter = NumericBooleanConverter.class)
    protected Boolean isCompany=Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_entity_type_id")
    protected Title legalEntityType;
    


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

    public Boolean getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(Boolean defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public String getProviderContact() {
        return providerContact;
    }

    public void setProviderContact(String providerContact) {
        this.providerContact = providerContact;
    }

    public ProviderContact getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(ProviderContact primaryContact) {
        this.primaryContact = primaryContact;
    }

    public BusinessAccountModel getBusinessAccountModel() {
        return businessAccountModel;
    }

    public void setBusinessAccountModel(BusinessAccountModel businessAccountModel) {
        this.businessAccountModel = businessAccountModel;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void anonymize(String code) {
        if (name != null ) {
            name.anonymize(code);
        }
        if (address != null ) {
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
     * Instantiate contactInformation field if it is null. NOTE: do not use this method unless you have an intention to modify it's value, as entity will be marked dirty and record
     * will be updated in DB
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
     * Expression to determine minimum amount value.
     * @return an El expression
     */
    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * Sets the minimum amount El expression.
     * @param minimumAmountEl an El expression
     */
    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * An EL expression to get the label used for the minimum RT.
     * @return an EL expression
     */
    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    /**
     * Sets the minimum amount label.
     * @param minimumLabelEl the minimum amount label EL
     */
    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    /**
     * Gets the charge template used in minimum amount.
     * @return a one Shot Charge template
     */
    public OneShotChargeTemplate getMinimumChargeTemplate() {
        return minimumChargeTemplate;
    }

    /**
     * Sets the minimum amount charge template.
     * @param minimumChargeTemplate a one Shot Charge template
     */
    public void setMinimumChargeTemplate(OneShotChargeTemplate minimumChargeTemplate) {
        this.minimumChargeTemplate = minimumChargeTemplate;
    }

    public AccountingArticle getMinimumArticle() {
        return minimumArticle;
    }

    public void setMinimumArticle(AccountingArticle minimumArticle) {
        this.minimumArticle = minimumArticle;
    }

	/**
	 * @return the isCompany
	 */
	public Boolean getIsCompany() {
		return isCompany != null && isCompany;
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
	public Title getLegalEntityType() {
		return legalEntityType;
	}

	/**
	 * @param legalEntityType the legalEntityType to set
	 */
	public void setLegalEntityType(Title legalEntityType) {
		this.legalEntityType = legalEntityType;
	}

    public String getAccountType() {
        return this.getClass().getSimpleName();
    }
}