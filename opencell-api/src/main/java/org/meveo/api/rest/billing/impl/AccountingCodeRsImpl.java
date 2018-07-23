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
import org.meveo.api.dto.response.billing.AccountingCodeListResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.AccountingCodeRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * REST API to manage AccountingCode or Chart of accounts.
 * 
 * @author Edward P. Legaspi
 * @version 23 Feb 2018
 * @lastModifiedVersion 5.0
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
    public AccountingCodeListResponseDto listGet(Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        AccountingCodeListResponseDto result = new AccountingCodeListResponseDto();

        PagingAndFiltering pagingAndFiltering = new PagingAndFiltering(null, null, offset, limit, sortBy, sortOrder);

        try {
            return accountingCodeApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public AccountingCodeListResponseDto listPost(PagingAndFiltering pagingAndFiltering) {
        AccountingCodeListResponseDto result = new AccountingCodeListResponseDto();

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

    @Override
    public ActionStatus enable(String code) {

        ActionStatus result = new ActionStatus();

        try {
            accountingCodeApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {

        ActionStatus result = new ActionStatus();

        try {
            accountingCodeApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
