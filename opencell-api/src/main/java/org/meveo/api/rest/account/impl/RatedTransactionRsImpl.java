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

package org.meveo.api.rest.account.impl;

import org.meveo.api.billing.RatedTransactionApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.RatedTransactionListResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.RatedTransactionRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.apiv2.generic.GenericPagingAndFilteringUtils;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

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
    public RatedTransactionListResponseDto list() {
        try {
            return ratedTransactionApi.listGetAll( GenericPagingAndFilteringUtils.getInstance().getPagingAndFiltering() );
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