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
import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class FindAccountHierachyRequestDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class FindAccountHierachyRequestDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9110625442489443755L;

    /**
     * Possible values. CUST = 1; CA = 2; BA = 4; UA = 8;
     **/
    private int level;
    
    /** The valid level values. */
    public static List<Integer> VALID_LEVEL_VALUES = Arrays.asList(1, 2, 4, 8);
    
    /** The name. */
    private NameDto name;
    
    /** The address. */
    private AddressDto address;

    /**
     * Gets the level.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the level.
     *
     * @param level the new level
     */
    public void setLevel(int level) {
        this.level = level;
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

}