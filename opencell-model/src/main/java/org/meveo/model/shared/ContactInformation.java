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
package org.meveo.model.shared;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

import org.meveo.commons.encryption.PersonnalDataEncryptor;

/**
 * Contact information
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Embeddable
public class ContactInformation implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    // @Pattern(regexp = ".+@.+\\..{2,4}")
    /**
     * Email
     */
    @Convert(converter=PersonnalDataEncryptor.class)
    @Column(name = "email", length = 2000)
    @Size(max = 2000)
    protected String email;

    /**
     * Phone number
     */
    @Convert(converter=PersonnalDataEncryptor.class)
    @Column(name = "phone", length = 100)
    @Size(max = 100)
    protected String phone;

    /**
     * Mobile phone number
     */
    @Convert(converter=PersonnalDataEncryptor.class)
    @Column(name = "mobile", length = 100)
    @Size(max = 100)
    protected String mobile;

    /**
     * Fax number
     */
    @Column(name = "fax", length = 50)
    @Size(max = 50)
    protected String fax;

    public ContactInformation() {
    }

    public ContactInformation(ContactInformation contactInformation) {
        this(contactInformation.email, contactInformation.phone, contactInformation.mobile, contactInformation.fax);
    }

    public ContactInformation(String email, String phone, String mobile, String fax) {
        super();
        this.email = email;
        this.phone = phone;
        this.mobile = mobile;
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void anonymize(String code) {
        setEmail(code + "@opencellsoft.com");
        setFax(code);
        setMobile(code);
        setPhone(code);
    }

}
