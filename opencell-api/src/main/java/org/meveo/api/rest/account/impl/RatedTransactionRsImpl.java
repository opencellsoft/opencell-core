package org.meveo.api.rest.account.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.billing.RatedTransactionApi;
import org.meveo.api.dto.billing.RatedTransactionListRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.RatedTransactionListResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.RatedTransactionRs;
import org.meveo.api.rest.impl.BaseRs;


/**
 * RatedTransactionRsImpl : Default implementation of Rated Transaction REST services
 * @author Said Ramli
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class RatedTransactionRsImpl extends BaseRs implements RatedTransactionRs {
    
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

}
