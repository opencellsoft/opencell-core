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
package org.meveo.model.crm;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.shared.Address;

/**
 * Provider contact information
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "crm_provider_contact", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_provider_contact_seq"), })
public class ProviderContact extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    /**
     * First name
     */
    @Column(name = "firstname", length = 50)
    @Size(max = 50)
    protected String firstName;

    /**
     * Last name
     */
    @Column(name = "lastname", length = 50)
    @Size(max = 50)
    protected String lastName;

    // @Pattern(regexp = ".+@.+\\..{2,4}")
    /**
     * Email address
     */
    @Column(name = "email", length = 100)
    @Size(max = 100)
    protected String email;

    /**
     * Phone
     */
    @Column(name = "phone", length = 50)
    @Size(max = 50)
    protected String phone;

    /**
     * Mobile phone number
     */
    @Column(name = "mobile", length = 50)
    @Size(max = 50)
    protected String mobile;

    /**
     * Fax number
     */
    @Column(name = "fax", length = 50)
    @Size(max = 50)
    protected String fax;

    // @Pattern(regexp = ".+@.+\\..{2,4}")
    /**
     * General email address
     */
    @Column(name = "generic_mail", length = 100)
    @Size(max = 100)
    protected String genericMail;

    /**
     * Address
     */
    @Embedded
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getGenericMail() {
        return genericMail;
    }

    public void setGenericMail(String genericMail) {
        this.genericMail = genericMail;
    }

}
