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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class NameDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Name")
@XmlAccessorType(XmlAccessType.FIELD)
public class NameDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4516337040269767031L;

    /** The title. */
    @Schema(description = "The title")
    private String title;
    
    /** The first name. */
    @Schema(description = "The first name")
    private String firstName;

    /** The last name. */
    @XmlElement(required = true)
    @Schema(description = "The last name", required = true)
    private String lastName;

    /**
     * Instantiates a new name dto.
     */
    public NameDto() {

    }

    /**
     * Instantiates a new name dto.
     *
     * @param name the name entity
     */
    public NameDto(org.meveo.model.shared.Name name) {
        if (name != null) {
            firstName = name.getFirstName();
            lastName = name.getLastName();
            if (name.getTitle() != null) {
                title = name.getTitle().getCode();
            }
        }
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
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

    @Override
    public String toString() {
        return "Name [title=" + title + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }

}