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
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.crm.ProviderContact;

/**
 * The Class ProviderContactDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author Edward P. Legaspi (edward.legaspi@manaty.net)
 */
@XmlRootElement(name = "ProviderContract")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProviderContactDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -763450889692487278L;
    
    /** The first name. */
    private String firstName;
    
    /** The last name. */
    private String lastName;
    
    /** The email. */
    private String email;
    
    /** The phone. */
    private String phone;
    
    /** The mobile. */
    private String mobile;
    
    /** The fax. */
    private String fax;
    
    /** The generic mail. */
    private String genericMail;

    /** The address dto. */
    private AddressDto addressDto;

    /**
     * Instantiates a new provider contact dto.
     */
    public ProviderContactDto() {
        super();
    }

    /**
     * Instantiates a new provider contact dto.
     *
     * @param providerContact the provider contact
     */
    public ProviderContactDto(ProviderContact providerContact) {
        super(providerContact);
        this.firstName = providerContact.getFirstName();
        this.lastName = providerContact.getLastName();
        this.email = providerContact.getEmail();
        this.phone = providerContact.getPhone();
        this.mobile = providerContact.getMobile();
        this.fax = providerContact.getFax();
        this.genericMail = providerContact.getGenericMail();
        if (providerContact.getAddress() != null) {
            this.addressDto = new AddressDto(providerContact.getAddress());
        }
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone.
     *
     * @param phone the new phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the mobile.
     *
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Sets the mobile.
     *
     * @param mobile the new mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * Gets the fax.
     *
     * @return the fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * Sets the fax.
     *
     * @param fax the new fax
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * Gets the generic mail.
     *
     * @return the generic mail
     */
    public String getGenericMail() {
        return genericMail;
    }

    /**
     * Sets the generic mail.
     *
     * @param genericMail the new generic mail
     */
    public void setGenericMail(String genericMail) {
        this.genericMail = genericMail;
    }

    /**
     * Gets the address dto.
     *
     * @return the address dto
     */
    public AddressDto getAddressDto() {
        return addressDto;
    }

    /**
     * Sets the address dto.
     *
     * @param addressDto the new address dto
     */
    public void setAddressDto(AddressDto addressDto) {
        this.addressDto = addressDto;
    }
}