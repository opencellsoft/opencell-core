/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

@Entity
@ObservableEntity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "crm_provider_contact", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_provider_contact_seq"), })
public class ProviderContact extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "firstname", length = 50)
    @Size(max = 50)
    protected String firstName;

    @Column(name = "lastname", length = 50)
    @Size(max = 50)
    protected String lastName;

    @Column(name = "email", length = 100)
    // @Pattern(regexp = ".+@.+\\..{2,4}")
    @Size(max = 100)
    protected String email;

    @Column(name = "phone", length = 15)
    @Size(max = 15)
    protected String phone;

    @Column(name = "mobile", length = 15)
    @Size(max = 15)
    protected String mobile;

    @Column(name = "fax", length = 15)
    @Size(max = 15)
    protected String fax;

    @Column(name = "generic_mail", length = 100)
    // @Pattern(regexp = ".+@.+\\..{2,4}")
    @Size(max = 100)
    protected String genericMail;

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
