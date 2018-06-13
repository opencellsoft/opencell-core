package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.AccountEntity;

/**
 * The Class AccountDto.
 *
 * @author Edward P. Legaspi
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

    /**
     * Instantiates a new account dto.
     */
    public AccountDto() {
        super();
    }
    
    /**
     * Instantiates a new account dto. Used on account hierarchy
     *
     * @param accountEntity the accountEntity entity
     */
    public AccountDto(AccountEntity accountEntity) {
        super(accountEntity);
    }

    /**
     * Instantiates a new account dto.
     *
     * @param accountEntity the accountEntity entity
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

}