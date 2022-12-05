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

package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class AccountOperationsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "AccountOperationsDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountOperationsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6969737909477126088L;

    /** The account operation. */
    private List<AccountOperationDto> accountOperation;

    /**
     * Gets the account operation.
     *
     * @return the account operation
     */
    public List<AccountOperationDto> getAccountOperation() {
        if (accountOperation == null) {
            accountOperation = new ArrayList<AccountOperationDto>();
        }
        return accountOperation;
    }

    /**
     * Sets the account operation.
     *
     * @param accountOperation the new account operation
     */
    public void setAccountOperation(List<AccountOperationDto> accountOperation) {
        this.accountOperation = accountOperation;
    }


    @Override
    public String toString() {
        return "AccountOperationsDto [accountOperation=" + accountOperation + "]";
    }

}