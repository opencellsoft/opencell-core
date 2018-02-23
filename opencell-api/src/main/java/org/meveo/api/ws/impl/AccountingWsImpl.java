package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.billing.AccountingCodeApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.AccountingWs;

/**
 * @author Edward P. Legaspi
 * @version 23 Feb 2018
 **/
@WebService(serviceName = "AccountingWs", endpointInterface = "org.meveo.api.ws.AccountingWs")
@Interceptors({ WsRestApiInterceptor.class })
public class AccountingWsImpl extends BaseWs implements AccountingWs {

    @Inject
    private AccountingCodeApi accountingCodeApi;

    @Override
    public ActionStatus createAccountingCode(AccountingCodeDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountingCodeApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeAccountingCode(String accountingCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountingCodeApi.remove(accountingCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
