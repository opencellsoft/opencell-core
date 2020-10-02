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

package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.AccountingCodeGetResponseDto;
import org.meveo.api.dto.response.billing.AccountingCodeListResponseDto;

/**
 * REST API to manage AccountingCode or Chart of accounts.
 *
 * @author Edward P. Legaspi
 * @version 23 Feb 2018
 * @lastModifiedVersion 5.0
 **/
@WebService
@Deprecated
public interface AccountingWs extends IBaseWs {

    /**
     * Creates a new AccountingCode.
     * 
     * @param postData The AccountingCode Dto
     * @return The ActionStatus Dto
     */
    @WebMethod
    ActionStatus createAccountingCode(@WebParam(name = "accountingCode") AccountingCodeDto postData);

    /**
     * Updates an AccountingCode.
     * 
     * @param postData The AccountingCode Dto
     * @return The ActionStatus Dto
     */
    @WebMethod
    ActionStatus updateAccountingCode(@WebParam(name = "accountingCode") AccountingCodeDto postData);

    /**
     * Creates or updates an AccountingCode.
     * 
     * @param postData The AccountingCode Dto
     * @return The ActionStatus Dto
     */
    @WebMethod
    ActionStatus createOrUpdateAccountingCode(@WebParam(name = "accountingCode") AccountingCodeDto postData);

    /**
     * Finds an AccountingCode.
     * 
     * @param accountingCode  The accounting code
     * @return The ActionStatus Dto
     */
    @WebMethod
    AccountingCodeGetResponseDto findAccountingCode(@WebParam(name = "accountingCode") String accountingCode);

    /**
     * Returns a list of AccountingCode.
     * 
     * @param pagingAndFiltering - Paging and Filtering criteria
     * @return The accounting code list dto
     */
    @WebMethod
    AccountingCodeListResponseDto listAccountingCode(@WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    /**
     * Removes an AccountingCode.
     * 
     * @param accountingCode The accounting code
     * @return The ActionStatus Dto
     */
    @WebMethod
    ActionStatus removeAccountingCode(@WebParam(name = "accountingCode") String accountingCode);

    /**
     * Enable an AccountingCode by its code
     * 
     * @param code Accounting code code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableAccountingCode(@WebParam(name = "code") String code);

    /**
     * Disable an AccountingCode by its code
     * 
     * @param code Accounting code code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableAccountingCode(@WebParam(name = "code") String code);
}
