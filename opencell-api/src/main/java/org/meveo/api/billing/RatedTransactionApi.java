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

package org.meveo.api.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.RatedTransactionListResponseDto;
import org.meveo.api.exception.ActionForbiddenException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.RatedTransactionService;

/**
 * RatedTransactionApi : An API for Rated transaction services.
 * 
 * @author Said Ramli
 * @author Mohamed El Youssoufi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class RatedTransactionApi extends BaseApi {

    /** The rated transaction service. */
    @Inject
    private RatedTransactionService ratedTransactionService;
    @Inject
    private AuditLogService auditLogService;

    /**
     * List Rated transactions given the filtering criteria
     * 
     * @param pagingAndFiltering Search and paging criteria. Pass "userAccountCode" as field option to retrieve associated User account's code.
     * @return A list of Rated transactions
     * @throws InvalidParameterException
     */
    public RatedTransactionListResponseDto list(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, RatedTransaction.class);
        Long totalCount = ratedTransactionService.count(paginationConfig);

        RatedTransactionListResponseDto result = new RatedTransactionListResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        boolean returnUserAccountCode = pagingAndFiltering.hasFieldOption("userAccountCode");
        boolean returnSellerCode = pagingAndFiltering.hasFieldOption("sellerCode");
        boolean returnInvoiceSubCategoryCode = pagingAndFiltering.hasFieldOption("invoiceSubCategoryCode");

        if (totalCount > 0) {
            List<RatedTransaction> ratedTransactions = ratedTransactionService.list(paginationConfig);
            for (RatedTransaction rt : ratedTransactions) {
                result.getRatedTransactions().add(new RatedTransactionDto(rt, returnUserAccountCode, returnSellerCode, returnInvoiceSubCategoryCode));
            }
        }
        return result;
    }

    /**
     * List Rated transactions given the filtering criteria
     *
     * @param pagingAndFiltering Search and paging criteria. Pass "userAccountCode" as field option to retrieve associated User account's code.
     * @return A list of Rated transactions
     * @throws InvalidParameterException
     */
    public RatedTransactionListResponseDto listGetAll(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {

        PaginationConfiguration paginationConfig = GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration();
        Long totalCount = ratedTransactionService.count(paginationConfig);

        RatedTransactionListResponseDto result = new RatedTransactionListResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        boolean returnUserAccountCode = pagingAndFiltering.hasFieldOption("userAccountCode");
        boolean returnSellerCode = pagingAndFiltering.hasFieldOption("sellerCode");
        boolean returnInvoiceSubCategoryCode = pagingAndFiltering.hasFieldOption("invoiceSubCategoryCode");

        if (totalCount > 0) {
            List<RatedTransaction> ratedTransactions = ratedTransactionService.list(paginationConfig);
            for (RatedTransaction rt : ratedTransactions) {
                result.getRatedTransactions().add(new RatedTransactionDto(rt, returnUserAccountCode, returnSellerCode, returnInvoiceSubCategoryCode));
            }
        }
        return result;
    }

    /**
     * 
     * Call Persistence Service to update passed RatedTransactions
     * 
     * @param pagingAndFiltering Query filtering
     * @throws InvalidParameterException can throw InvalidParameterException
     */
    public void cancelRatedTransactions(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {

        List<RatedTransaction> ratedTransactions = getRatedTransactionsFromPaginationConfig(pagingAndFiltering);

        if(!ratedTransactions.isEmpty()) {
            if (!canCancelRatedTransactions(ratedTransactions)) {
                throw new InvalidParameterException("Only rated transactions in statuses OPEN, REJECTED can be cancelled");
            }

            List<Long> ratedTransactionsToCancel = retreiveRatedTrasactionsIdsToCancel(ratedTransactions);

            ratedTransactionService.cancelRatedTransactions(ratedTransactionsToCancel);
            Date dateOperation = new Date();
            String ids = ratedTransactionsToCancel.size() == 1 ? "id = " + ratedTransactionsToCancel.get(0).toString() : "ids " + ratedTransactionsToCancel.toString();
            String detail = auditLogService.getDefaultMessage(RatedTransactionStatusEnum.CANCELED.name(), dateOperation, new RatedTransaction(), ids, null);
            auditLogService.trackOperation(RatedTransactionStatusEnum.CANCELED.name(), dateOperation, new RatedTransaction(), null, detail);
        }

    }

    private List<RatedTransaction> getRatedTransactionsFromPaginationConfig(PagingAndFiltering pagingAndFiltering) {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration(pagingAndFiltering.getSortBy(), SortOrder.ASCENDING, null, pagingAndFiltering, RatedTransaction.class);
        return ratedTransactionService.list(paginationConfig);
    }

    private boolean canCancelRatedTransactions(List<RatedTransaction> ratedTransactions) {

        return ratedTransactions.stream().allMatch(ratedTransaction ->
                (ratedTransaction.getStatus().equals(RatedTransactionStatusEnum.OPEN)) ||
                        (ratedTransaction.getStatus().equals(RatedTransactionStatusEnum.REJECTED)));

    }

    /**
     * 
     * Retrieves, filter and construct a list of Rated Transactions ids to cancel according to query and PagingAndFiltering values.
     * 
     * @return list of Rated Transactions ids to cancel
     * @throws InvalidParameterException can throw invalid parameter Exception
     */
    private List<Long> retreiveRatedTrasactionsIdsToCancel(List<RatedTransaction> ratedTransactions) throws InvalidParameterException, ActionForbiddenException {

        List<Long> rsToCancelIds = new ArrayList<Long>(ratedTransactions.size());
        for (RatedTransaction rt : ratedTransactions) {
            rsToCancelIds.add(rt.getId());
        }
        return rsToCancelIds;
    }
}