package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.AccountingCodeDto;

/**
 * @author Edward P. Legaspi
 * @version 23 Feb 2018
 **/
@WebService
public interface AccountingWs extends IBaseWs {

    /**
     * Creates a new AccountingCode.
     * 
     * @param postData
     * @return
     */
    @WebMethod
    ActionStatus createAccountingCode(@WebParam(name = "accountingCode") AccountingCodeDto postData);
    
    @WebMethod
    ActionStatus removeAccountingCode(@WebParam(name = "accountingCode") String accountingCode);

}
