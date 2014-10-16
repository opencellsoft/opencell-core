/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.service.api.dto;

/**
 * Contains information about a user account
 * 
 * @author Andrius Karpavicius
 * 
 */
public class UserAccountDTO extends AccountDTO {

    private static final long serialVersionUID = -8444588283337483623L;

    private String billingAccountCode;

    public UserAccountDTO(Long id, String code, String externalRef1, String externalRef2, AddressDTO address, String titleCode, String firstName, String lastName,
            String billingAccountCode) {
        super(id, code, externalRef1, externalRef2, address, titleCode, firstName, lastName);

        this.billingAccountCode = billingAccountCode;
    }

    public String getBillingAccountCode() {
        return billingAccountCode;
    }
}