package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.catalog.ServiceTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetListServiceTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.ServiceTemplateRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Edward P. Legaspi
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ServiceTemplateRsImpl extends BaseRs implements ServiceTemplateRs {

    @Inject
    private ServiceTemplateApi serviceTemplateApi;

    @Override
    public ActionStatus create(ServiceTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            serviceTemplateApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(ServiceTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            serviceTemplateApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetServiceTemplateResponseDto find(String serviceTemplateCode, CustomFieldInheritanceEnum inheritCF) {
        GetServiceTemplateResponseDto result = new GetServiceTemplateResponseDto();

        try {
            result.setServiceTemplate(serviceTemplateApi.find(serviceTemplateCode, inheritCF));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String serviceTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            serviceTemplateApi.remove(serviceTemplateCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(ServiceTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            serviceTemplateApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            serviceTemplateApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            serviceTemplateApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetListServiceTemplateResponseDto list(PagingAndFiltering pagingAndFiltering) {
        GetListServiceTemplateResponseDto result = new GetListServiceTemplateResponseDto();

        try {
            return serviceTemplateApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
}