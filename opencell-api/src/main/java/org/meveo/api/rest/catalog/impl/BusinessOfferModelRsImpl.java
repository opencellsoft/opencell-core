package org.meveo.api.rest.catalog.impl;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.dto.response.catalog.GetBusinessOfferModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.module.MeveoModuleApi;
import org.meveo.api.rest.catalog.BusinessOfferModelRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.catalog.BusinessOfferModel;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class BusinessOfferModelRsImpl extends BaseRs implements BusinessOfferModelRs {

    @Inject
    private MeveoModuleApi moduleApi;

    @Override
    public ActionStatus create(BusinessOfferModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(BusinessOfferModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetBusinessOfferModelResponseDto find(String businessOfferModelCode) {
        GetBusinessOfferModelResponseDto result = new GetBusinessOfferModelResponseDto();

        try {
            result.setBusinessOfferModel((BusinessOfferModelDto) moduleApi.find(businessOfferModelCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String businessOfferModelCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.remove(businessOfferModelCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(BusinessOfferModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse list() {
        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        result.getActionStatus().setMessage("");
        try {
            List<MeveoModuleDto> dtos = moduleApi.list(BusinessOfferModel.class);
            result.setModules(dtos);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus install(BusinessOfferModelDto moduleDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            moduleApi.install(moduleDto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}