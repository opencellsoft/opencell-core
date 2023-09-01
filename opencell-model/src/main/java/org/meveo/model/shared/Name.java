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

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.meveo.commons.encryption.PersonnalDataEncryptor;

/**
 * Name
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Embeddable
public class Name implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * Title
     */
    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "title_id")
    @Transient
    private Title title;

    /**
     * First name or company name
     */
    @Convert(converter=PersonnalDataEncryptor.class)
    @Column(name = "firstname", length = 100)
    @Size(max = 100)
    protected String firstName;

    /**
     * Last name
     */
    @Convert(converter=PersonnalDataEncryptor.class)
    @Column(name = "lastname", length = 100)
    @Size(max = 100)
    protected String lastName;

    public Name() {
    }

    public Name(Title title, String firstName, String lastName) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
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

    @Override
    public String toString() {
        return (title != null ? ((title.getDescription() != null ? title.getDescription() : title.getCode()) + " " + (firstName != null ? firstName : "")
                + (lastName != null ? " " + lastName : "")) : "");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getFullName() {
        return "" + (title != null ? (title.getDescription() != null ? title.getDescription() : title.getCode()) + " " : "") + (firstName != null ? firstName + " " : "")
                + (lastName != null ? lastName : "");
    }

    public void anonymize(String code) {
        setFirstName(code);
        setLastName(code);
    }
}
