package org.meveo.api.rest.account.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.billing.RatedTransactionApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.RatedTransactionListRequestDto;
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

    /**
     * final Integer 0.
     */
    private static final Integer ZERO_INTEGER = new Integer(0);

    /**
     * used sort by field.
     */
    private static final String SORT_BY_FIELD_CODE = "code";

    @Inject
    private RatedTransactionApi ratedTransactionApi;

    @Override
    public RatedTransactionListResponseDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder, Boolean returnUserAccountCode) {
        try {
            return ratedTransactionApi.list(new RatedTransactionListRequestDto(query, fields, offset, limit, sortBy, sortOrder, returnUserAccountCode));
        } catch (Exception e) {
            RatedTransactionListResponseDto result = new RatedTransactionListResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public RatedTransactionListResponseDto listPost(RatedTransactionListRequestDto postData) {
        try {
            return ratedTransactionApi.list(postData);
        } catch (Exception e) {
            RatedTransactionListResponseDto result = new RatedTransactionListResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    /*
     * @see org.meveo.api.rest.billing.RatedTransactionRs#cancelRatedTransactions(java.lang.String)
     */
    @Override
    public ActionStatus cancelRatedTransactions(String query) {

        ActionStatus actionStatus = new ActionStatus();

        try {
            RatedTransactionListRequestDto ratedTransactionListRequestDto = new RatedTransactionListRequestDto(query, null, ZERO_INTEGER, Integer.MAX_VALUE, SORT_BY_FIELD_CODE,
                SortOrder.ASCENDING, false);
            ratedTransactionApi.cancelRatedTransactions(ratedTransactionListRequestDto);
            actionStatus.setStatus(ActionStatusEnum.SUCCESS);
        } catch (Exception e) {
            actionStatus.setStatus(ActionStatusEnum.FAIL);
            actionStatus.setMessage(e.getLocalizedMessage());
            processException(e, actionStatus);
        }

        return actionStatus;
    }

}
