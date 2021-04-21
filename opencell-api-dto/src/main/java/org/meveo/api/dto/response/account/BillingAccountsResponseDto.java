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

package org.meveo.api.dto.response.account;

import org.meveo.api.dto.account.BillingAccountsDto;
import org.meveo.api.dto.response.SearchResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class BillingAccountsResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "BillingAccountsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingAccountsResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 583740580596077812L;

    /** The billing accounts. */
    private BillingAccountsDto billingAccounts = new BillingAccountsDto();

    /**
     * Gets the billing accounts.
     *
     * @return the billing accounts
     */
    public BillingAccountsDto getBillingAccounts() {
        return billingAccounts;
    }

    /**
     * Sets the billing accounts.
     *
     * @param billingAccounts the new billing accounts
     */
    public void setBillingAccounts(BillingAccountsDto billingAccounts) {
        this.billingAccounts = billingAccounts;
    }

    @Override
    public String toString() {
        return "ListBillingAccountResponseDto [billingAccounts=" + billingAccounts + ", toString()=" + super.toString() + "]";
    }
}