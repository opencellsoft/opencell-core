package org.meveo.api.rest.tax.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.tax.TaxCategoryListResponseDto;
import org.meveo.api.dto.response.tax.TaxCategoryResponseDto;
import org.meveo.api.dto.tax.TaxCategoryDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tax.TaxCategoryRs;
import org.meveo.api.tax.TaxCategoryApi;

/**
 * REST interface definition of Tax category API
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class TaxCategoryRsImpl extends BaseRs implements TaxCategoryRs {

    @Inject
    private TaxCategoryApi apiService;

    @Override
    public ActionStatus create(TaxCategoryDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.create(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TaxCategoryResponseDto find(String code) {
        TaxCategoryResponseDto result = new TaxCategoryResponseDto();

        try {
            result.setDto(apiService.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus update(TaxCategoryDto dto) {
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
    public ActionStatus createOrUpdate(TaxCategoryDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.createOrUpdate(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TaxCategoryListResponseDto searchGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        TaxCategoryListResponseDto result;

        try {
            result = new TaxCategoryListResponseDto(apiService.search(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder)));
        } catch (Exception e) {
            result = new TaxCategoryListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public TaxCategoryListResponseDto searchPost(PagingAndFiltering pagingAndFiltering) {

        TaxCategoryListResponseDto result;

        try {
            result = new TaxCategoryListResponseDto(apiService.search(pagingAndFiltering));
        } catch (Exception e) {
            result = new TaxCategoryListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }
}