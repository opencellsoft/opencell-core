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

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;

/**
 * The Class ContactInformationDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactInformationDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5401291437074205081L;

    /** The email. */
    protected String email;
    
    /** The phone. */
    protected String phone;
    
    /** The mobile. */
    protected String mobile;
    
    /** The fax. */
    protected String fax;
    
    /** address **/
    protected AddressDto address;
    
    

    /**
     * Instantiates a new contact information dto.
     */
    public ContactInformationDto() {

    }

    /**
     * Instantiates a new contact information dto.
     *
     * @param contactInformation the contactInformation entity
     */
    public ContactInformationDto(ContactInformation contactInformation) {
        email = contactInformation.getEmail();
        phone = contactInformation.getPhone();
        mobile = contactInformation.getMobile();
        fax = contactInformation.getFax();
    }
    
    public ContactInformationDto(ContactInformation contactInformation, Address address) {
    	this(contactInformation);
    	if(address != null) {
    		this.address  = new AddressDto(address);
    	}
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

    @Override
    public String toString() {
        return "ContactInformationDto [email=" + email + ", phone=" + phone + ", mobile=" + mobile + ", fax=" + fax + "]";
    }

	/**
	 * @return the address
	 */
	public AddressDto getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(AddressDto address) {
		this.address = address;
	}
}
