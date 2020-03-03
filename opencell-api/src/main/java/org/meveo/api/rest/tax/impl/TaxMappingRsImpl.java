package org.meveo.api.rest.tax.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.tax.TaxMappingListResponseDto;
import org.meveo.api.dto.response.tax.TaxMappingResponseDto;
import org.meveo.api.dto.tax.TaxMappingDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tax.TaxMappingRs;
import org.meveo.api.tax.TaxMappingApi;

/**
 * REST interface definition of Tax mapping API
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class TaxMappingRsImpl extends BaseRs implements TaxMappingRs {

    @Inject
    private TaxMappingApi apiService;

    @Override
    public ActionStatus create(TaxMappingDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.create(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TaxMappingResponseDto find(String code) {
        TaxMappingResponseDto result = new TaxMappingResponseDto();

        try {
            result.setDto(apiService.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus update(TaxMappingDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.update(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(TaxMappingDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.createOrUpdate(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TaxMappingListResponseDto searchGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        TaxMappingListResponseDto result;

        try {
            result = new TaxMappingListResponseDto(apiService.search(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder)));
        } catch (Exception e) {
            result = new TaxMappingListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public TaxMappingListResponseDto searchPost(PagingAndFiltering pagingAndFiltering) {

        TaxMappingListResponseDto result;

        try {
            result = new TaxMappingListResponseDto(apiService.search(pagingAndFiltering));
        } catch (Exception e) {
            result = new TaxMappingListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }
}