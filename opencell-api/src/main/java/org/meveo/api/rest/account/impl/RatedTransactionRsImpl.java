package org.meveo.api.rest.account.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.billing.RatedTransactionApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.RatedTransactionListResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.RatedTransactionRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * RatedTransactionRsImpl : Default implementation of Rated Transaction REST services.
 * 
 * @author Said Ramli
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 5.2
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class RatedTransactionRsImpl extends BaseRs implements RatedTransactionRs {

    @Inject
    private RatedTransactionApi ratedTransactionApi;

    @Override
    public RatedTransactionListResponseDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder, Boolean returnUserAccountCode) {
        try {

            if (returnUserAccountCode != null && returnUserAccountCode) {
                fields = (fields != null ? fields + ", " : "") + "userAccountCode";
            }

            return ratedTransactionApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            RatedTransactionListResponseDto result = new RatedTransactionListResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public RatedTransactionListResponseDto listPost(PagingAndFiltering pagingAndFiltering) {
        try {
            return ratedTransactionApi.list(pagingAndFiltering);
        } catch (Exception e) {
            RatedTransactionListResponseDto result = new RatedTransactionListResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public ActionStatus cancelRatedTransactions(PagingAndFiltering pagingAndFiltering) {

        ActionStatus actionStatus = new ActionStatus();

        try {
            ratedTransactionApi.cancelRatedTransactions(pagingAndFiltering);
            actionStatus.setStatus(ActionStatusEnum.SUCCESS);
        } catch (Exception e) {
            actionStatus.setStatus(ActionStatusEnum.FAIL);
            actionStatus.setMessage(e.getLocalizedMessage());
            processException(e, actionStatus);
        }

        return actionStatus;
    }
}