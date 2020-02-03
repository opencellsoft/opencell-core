package org.meveo.api.rest.catalog.impl;

import org.meveo.api.catalog.UnitOfMeasureApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.UnitOfMeasureDto;
import org.meveo.api.dto.response.catalog.GetListUnitOfMeasureResponseDto;
import org.meveo.api.dto.response.catalog.GetUnitOfMeasureResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.UnitOfMeasureRs;
import org.meveo.api.rest.impl.BaseRs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
/**
 * @author Mounir Bahije
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class UnitOfMeasureRsImpl extends BaseRs implements UnitOfMeasureRs {

    @Inject
    private UnitOfMeasureApi unitOfMeasureApi;

    @Override
    public ActionStatus create(UnitOfMeasureDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            unitOfMeasureApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(UnitOfMeasureDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            unitOfMeasureApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetUnitOfMeasureResponseDto find(String unitOfMeasureCode) {
        GetUnitOfMeasureResponseDto result = new GetUnitOfMeasureResponseDto();

        try {
            result.setUnitOfMeasure(unitOfMeasureApi.find(unitOfMeasureCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus delete(String unitOfMeasureCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            unitOfMeasureApi.remove(unitOfMeasureCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(UnitOfMeasureDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            unitOfMeasureApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetListUnitOfMeasureResponseDto list() {
        GetListUnitOfMeasureResponseDto result = new GetListUnitOfMeasureResponseDto();

        try {
            result.setListUnitOfMeasure(unitOfMeasureApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}