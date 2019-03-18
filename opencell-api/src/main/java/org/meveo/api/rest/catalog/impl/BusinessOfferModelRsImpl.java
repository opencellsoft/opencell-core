package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.module.ModulePropertyFlagLoader;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetBusinessOfferModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.module.MeveoModuleApi;
import org.meveo.api.rest.catalog.BusinessOfferModelRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.BusinessOfferModel;
import org.meveo.model.module.MeveoModule;

/**
 * @author Edward P. Legaspi(edward.legaspi@manaty.net)
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class BusinessOfferModelRsImpl extends BaseRs implements BusinessOfferModelRs {

    @Inject
    private MeveoModuleApi moduleApi;

    @Inject
    private OfferTemplateApi offerTemplateApi;

    @Override
    public ActionStatus create(BusinessOfferModelDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            MeveoModule meveoModule = moduleApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(meveoModule.getCode());
            }
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
    public GetBusinessOfferModelResponseDto find(String businessOfferModelCode, boolean loadOfferServiceTemplate, boolean loadOfferProductTemplate,
            boolean loadServiceChargeTemplate, boolean loadProductChargeTemplate) {
        GetBusinessOfferModelResponseDto result = new GetBusinessOfferModelResponseDto();
        ModulePropertyFlagLoader modulePropertyFlagLoader = new ModulePropertyFlagLoader();
        modulePropertyFlagLoader.setLoadOfferServiceTemplate(loadOfferServiceTemplate);
        modulePropertyFlagLoader.setLoadOfferProductTemplate(loadOfferProductTemplate);
        modulePropertyFlagLoader.setLoadServiceChargeTemplate(loadServiceChargeTemplate);
        modulePropertyFlagLoader.setLoadProductChargeTemplate(loadProductChargeTemplate);
        
        try {
            result.setBusinessOfferModel((BusinessOfferModelDto) moduleApi.find(businessOfferModelCode, modulePropertyFlagLoader));
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
            if (postData != null) {
                if (postData.getOfferTemplate() != null) {
                    if (offerTemplateApi.find(postData.getOfferTemplate().getCode(), null, null) != null) {
                        MeveoModule meveoModule = moduleApi.createOrUpdate(postData);
                        if (StringUtils.isBlank(postData.getCode())) {
                            result.setEntityCode(meveoModule.getCode());
                        }
                    }
                }
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse listGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();
        
        try {
            result = moduleApi.list(BusinessOfferModel.class, new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public MeveoModuleDtosResponse listPost(PagingAndFiltering pagingAndFiltering) {
        MeveoModuleDtosResponse result = new MeveoModuleDtosResponse();

        try {
            result = moduleApi.list(BusinessOfferModel.class, pagingAndFiltering);

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