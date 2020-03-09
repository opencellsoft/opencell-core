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