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
package org.meveo.service.api.dto;

/**
 * Contains information about a user account.
 * 
 * @author Andrius Karpavicius
 * 
 */
public class UserAccountDTO extends AccountDTO {

    private static final long serialVersionUID = -8444588283337483623L;

    /**
     * billing account code.
     */
    private String billingAccountCode;

    /**
     * @param id id of user account
     * @param code code of user account
     * @param externalRef1 external reference 1
     * @param externalRef2 external reference 2
     * @param address address
     * @param titleCode code of title
     * @param firstName first name
     * @param lastName last name.
     * @param billingAccountCode billing account code
     */
    public UserAccountDTO(Long id, String code, String externalRef1, String externalRef2, AddressDTO address, String titleCode, String firstName, String lastName,
            String billingAccountCode) {
        super(id, code, externalRef1, externalRef2, address, titleCode, firstName, lastName);

        this.billingAccountCode = billingAccountCode;
    }

    /**
     * @return billing account.
     */
    public String getBillingAccountCode() {
        return billingAccountCode;
    }
}