package org.meveo.api.rest.billing.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.billing.AccountingCodeApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.AccountingCodeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.AccountingCodeGetResponseDto;
import org.meveo.api.dto.response.billing.AccountingCodeListResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.AccountingCodeRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 * @version 23 Feb 2018
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class AccountingCodeRsImpl extends BaseRs implements AccountingCodeRs {

    @Inject
    private AccountingCodeApi accountingCodeApi;

    @Override
    public ActionStatus create(AccountingCodeDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountingCodeApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(AccountingCodeDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountingCodeApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(AccountingCodeDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountingCodeApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public AccountingCodeGetResponseDto find(String accountingCode) {
        AccountingCodeGetResponseDto result = new AccountingCodeGetResponseDto();

        try {
            result.setAccountingCode(accountingCodeApi.find(accountingCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public AccountingCodeListResponse listGet(Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        AccountingCodeListResponse result = new AccountingCodeListResponse();

        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering(null, null, offset, limit, sortBy, sortOrder);

        try {
            return accountingCodeApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public AccountingCodeListResponse listPost(PagingAndFiltering pagingAndFiltering) {
        AccountingCodeListResponse result = new AccountingCodeListResponse();

        try {
            return accountingCodeApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String accountingCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            accountingCodeApi.remove(accountingCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}
