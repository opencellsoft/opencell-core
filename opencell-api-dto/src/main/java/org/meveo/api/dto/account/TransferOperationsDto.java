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
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the data necessary to establish an account operation transfer from one customer to another .
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@XmlRootElement(name = "TransferOperationsDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransferOperationsDto implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -2310786528437213936L;

    /**
     * The source customer account.
     */
    @XmlElement(required = true)
    private Set<Long> accountOperationsList;

    /**
     * The recipient customer accounts.
     */
    @XmlElement(required = true)
    private CustomerToTransfertOperationDto toCustomerAccount;

    public Set<Long> getAccountOperationsList() {
        return accountOperationsList;
    }

    public CustomerToTransfertOperationDto getToCustomerAccount() {
        return toCustomerAccount;
    }

    public void setAccountOperationsList(Set<Long> accountOperationsList) {
        this.accountOperationsList = accountOperationsList;
    }

    public void setToCustomerAccount(CustomerToTransfertOperationDto toCustomerAccount) {
        this.toCustomerAccount = toCustomerAccount;
    }

    
}