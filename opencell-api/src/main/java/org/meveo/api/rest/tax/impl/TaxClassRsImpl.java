package org.meveo.api.rest.tax.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.tax.TaxClassListResponseDto;
import org.meveo.api.dto.response.tax.TaxClassResponseDto;
import org.meveo.api.dto.tax.TaxClassDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.tax.TaxClassRs;
import org.meveo.api.tax.TaxClassApi;

/**
 * REST interface definition of Tax class API
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class TaxClassRsImpl extends BaseRs implements TaxClassRs {

    @Inject
    private TaxClassApi apiService;

    @Override
    public ActionStatus create(TaxClassDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.create(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TaxClassResponseDto find(String code) {
        TaxClassResponseDto result = new TaxClassResponseDto();

        try {
            result.setDto(apiService.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus update(TaxClassDto dto) {
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
    public ActionStatus createOrUpdate(TaxClassDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            apiService.createOrUpdate(dto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TaxClassListResponseDto searchGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {

        TaxClassListResponseDto result;

        try {
            result = new TaxClassListResponseDto(apiService.search(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder)));
        } catch (Exception e) {
            result = new TaxClassListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public TaxClassListResponseDto searchPost(PagingAndFiltering pagingAndFiltering) {

        TaxClassListResponseDto result;

        try {
            result = new TaxClassListResponseDto(apiService.search(pagingAndFiltering));
        } catch (Exception e) {
            result = new TaxClassListResponseDto();
            processException(e, result.getActionStatus());
        }

        return result;
    }
}