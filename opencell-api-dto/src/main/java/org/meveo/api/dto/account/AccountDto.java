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

package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.AccountEntity;
import org.meveo.model.catalog.OneShotChargeTemplate;

/**
 * The Class AccountDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@XmlRootElement(name = "Account")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AccountDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8818317499795113026L;

    /** The external ref 1. */
    private String externalRef1;
    
    /** The external ref 2. */
    private String externalRef2;
    
    /** The name. */
    private NameDto name;
    
    /** The address. */
    private AddressDto address;
    
    /** The job title. */
    private String jobTitle;

    /** The business account model. */
    @XmlElement(name = "businessAccountModel")
    private BusinessEntityDto businessAccountModel;
    
    /** The custom fields. */
    private CustomFieldsDto customFields;

    /** The loaded. */
    @XmlTransient
    protected boolean loaded = false;
    
    /** The vat no. */
    private String vatNo;
    
    /** The registration no. */
    private String registrationNo;

    /** The contact information. */
    private ContactInformationDto contactInformation;

    /**
     * Expression to determine minimum amount value
     */
    private String minimumAmountEl;

    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    private String minimumLabelEl;

    /**
     * The billing account code to be used when calculating the min amount billable for Customer and CA.
     */
    private String minimumTargetAccount;

    /**
     * Expression to determine minimum amount value - for Spark.
     */
    private String minimumAmountElSpark;

    /**
     * Expression to determine rated transaction description to reach minimum amount value - for Spark.
     */
    private String minimumLabelElSpark;

    /**
     * Corresponding to minimum one shot charge template code.
     */
    private String minimumChargeTemplate;

    /**
     * Instantiates a new account dto.
     */
    public AccountDto() {
        super();
    }

    /**
     * Instantiates a new account dto. Used on account hierarchy
     *
     * @param e the accountEntity entity
     */
    public AccountDto(AccountEntity e) {
        super(e);

        setVatNo(e.getVatNo());
        setRegistrationNo(e.getVatNo());
        if (e.getContactInformation() != null) {
            setContactInformation(new ContactInformationDto(e.getContactInformation(), e.getAddress()));
        }
        if (e.getMinimumChargeTemplate() != null){
            setMinimumChargeTemplate(e.getMinimumChargeTemplate().getCode());
        }
    }

    /**
     * Instantiates a new account dto.
     *
     * @param accountEntity        the accountEntity entity
     * @param customFieldInstances the custom field instances
     */
    public AccountDto(AccountEntity accountEntity, CustomFieldsDto customFieldInstances) {
        super(accountEntity);
        initFromEntity(accountEntity, customFieldInstances);
    }

    /**
     * Inits the from entity.
     *
     * @param account the account
     * @param customFieldInstances the custom field instances
     */
    public void initFromEntity(AccountEntity account, CustomFieldsDto customFieldInstances) {
        setAuditable(account);
        setExternalRef1(account.getExternalRef1());
        setExternalRef2(account.getExternalRef2());
        if (account.getName() != null) {
            setName(new NameDto(account.getName()));
        }
        if (account.getAddress() != null) {
            setAddress(new AddressDto(account.getAddress()));
        }

        customFields = customFieldInstances;

        loaded = true;
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
     * Gets the business account model.
     *
     * @return the business account model
     */
    public BusinessEntityDto getBusinessAccountModel() {
        return businessAccountModel;
    }

    /**
     * Sets the business account model.
     *
     * @param businessAccountModel the new business account model
     */
    public void setBusinessAccountModel(BusinessEntityDto businessAccountModel) {
        this.businessAccountModel = businessAccountModel;
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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        return result;
    }

    /* (non-Javadoc)
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
    
    @Override
    public String toString() {
        return "AccountDto [code=" + getCode() + ", description=" + getDescription() + ", externalRef1=" + externalRef1 + ", externalRef2=" + externalRef2 + ", name=" + name
                + ", address=" + address + ", customFields=" + customFields + ", loaded=" + loaded + ", businessAccountModel=" + businessAccountModel + "]";
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

    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    public String getMinimumTargetAccount() {
        return minimumTargetAccount;
    }

    public void setMinimumTargetAccount(String minimumTargetAccount) {
        this.minimumTargetAccount = minimumTargetAccount;
    }

    public String getMinimumAmountElSpark() {
        return minimumAmountElSpark;
    }

    public void setMinimumAmountElSpark(String minimumAmountElSpark) {
        this.minimumAmountElSpark = minimumAmountElSpark;
    }

    public String getMinimumLabelElSpark() {
        return minimumLabelElSpark;
    }

    public void setMinimumLabelElSpark(String minimumLabelElSpark) {
        this.minimumLabelElSpark = minimumLabelElSpark;
    }

    public String getMinimumChargeTemplate() {
        return minimumChargeTemplate;
    }

    public void setMinimumChargeTemplate(String minimumChargeTemplate) {
        this.minimumChargeTemplate = minimumChargeTemplate;
    }
}