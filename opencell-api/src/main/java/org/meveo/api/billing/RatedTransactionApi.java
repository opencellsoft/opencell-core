package org.meveo.api.billing;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.RatedTransactionDto;
import org.meveo.api.dto.account.RatedTransactionListDto;
import org.meveo.api.dto.billing.RatedTransactionListRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.RatedTransactionListResponseDto;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.primefaces.model.SortOrder;

/**
 * RatedTransactionApi : An API for Rated transaction services
 * 
 * @author Said Ramli
 */
@Stateless
public class RatedTransactionApi extends BaseApi {

    /** The rated transaction service. */
    @Inject
    RatedTransactionService ratedTransactionService;

    public RatedTransactionListResponseDto list(RatedTransactionListRequestDto postData) throws InvalidParameterException {

        PagingAndFiltering pagingAndFiltering = postData.getPagingAndFiltering();
        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, RatedTransaction.class);
        Long totalCount = ratedTransactionService.count(paginationConfig);

        RatedTransactionListResponseDto result = new RatedTransactionListResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        RatedTransactionListDto ratedTransactionListDto = new RatedTransactionListDto();
        ratedTransactionListDto.setTotalNumberOfRecords(totalCount);

        if (totalCount > 0) {
            List<RatedTransaction> ratedTransactions = ratedTransactionService.list(paginationConfig);
            for (RatedTransaction rt : ratedTransactions) {
                ratedTransactionListDto.getRatedTransactions().add(new RatedTransactionDto(rt, postData.getReturnUserAccountCode()));
            }
        }
        result.setRatedTransactionListDto(ratedTransactionListDto);
        return result;
    }

}
