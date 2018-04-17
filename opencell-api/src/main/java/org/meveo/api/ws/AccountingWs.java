package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.AccountingCodeGetResponseDto;
import org.meveo.api.dto.response.billing.AccountingCodeListResponse;

/**
 * REST API to manage AccountingCode or Chart of accounts.
 *
 * @author Edward P. Legaspi
 * @version 23 Feb 2018
 * @lastModifiedVersion 5.0
 **/
@WebService
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
    AccountingCodeListResponse listAccountingCode(@WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    /**
     * Removes an AccountingCode.
     * 
     * @param accountingCode The accounting code
     * @return The ActionStatus Dto
     */
    @WebMethod
    ActionStatus removeAccountingCode(@WebParam(name = "accountingCode") String accountingCode);

}
