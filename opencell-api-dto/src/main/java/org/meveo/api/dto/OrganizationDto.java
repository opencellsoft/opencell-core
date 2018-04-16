package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class OrganizationDto.
 *
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 */
@XmlRootElement(name = "Organization")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrganizationDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1477234763625399418L;
    
    /** The organization id. */
    private String organizationId;
    
    /** The name. */
    private String name;
    
    /** The parent id. */
    private String parentId;
    
    /** The country code. */
    private String countryCode;
    
    /** The default currency code. */
    private String defaultCurrencyCode;
    
    /** The language code. */
    private String languageCode;

    /**
     * Gets the organization id.
     *
     * @return the organization id
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Sets the organization id.
     *
     * @param organizationId the new organization id
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the parent id.
     *
     * @return the parent id
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * Sets the parent id.
     *
     * @param parentId the new parent id
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Gets the country code.
     *
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the country code.
     *
     * @param countryCode the new country code
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * Gets the default currency code.
     *
     * @return the default currency code
     */
    public String getDefaultCurrencyCode() {
        return defaultCurrencyCode;
    }

    /**
     * Sets the default currency code.
     *
     * @param defaultCurrencyCode the new default currency code
     */
    public void setDefaultCurrencyCode(String defaultCurrencyCode) {
        this.defaultCurrencyCode = defaultCurrencyCode;
    }

    /**
     * Gets the language code.
     *
     * @return the language code
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * Sets the language code.
     *
     * @param languageCode the new language code
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

}
