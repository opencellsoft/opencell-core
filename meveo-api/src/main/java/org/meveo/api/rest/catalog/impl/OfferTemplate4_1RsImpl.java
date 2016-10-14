package org.meveo.api.rest.catalog.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OfferServiceTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplate4_1Dto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.OfferTemplate4_1Rs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class OfferTemplate4_1RsImpl extends BaseRs implements OfferTemplate4_1Rs {

    @Inject
    private OfferTemplateApi offerTemplateApi;

    @Override
    public ActionStatus create(OfferTemplate4_1Dto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        OfferTemplateDto offerTemplateDto = convertOfferTemplateDto(postData);

        try {
            offerTemplateApi.create(offerTemplateDto, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus update(OfferTemplate4_1Dto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        OfferTemplateDto offerTemplateDto = convertOfferTemplateDto(postData);

        try {
            offerTemplateApi.update(offerTemplateDto, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public GetOfferTemplateResponseDto find(String offerTemplateCode) {
        GetOfferTemplateResponseDto result = new GetOfferTemplateResponseDto();

        try {
            result.setOfferTemplate(offerTemplateApi.find(offerTemplateCode, getCurrentUser()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String offerTemplateCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            offerTemplateApi.remove(offerTemplateCode, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(OfferTemplate4_1Dto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        OfferTemplateDto offerTemplateDto = convertOfferTemplateDto(postData);

        try {
            offerTemplateApi.createOrUpdate(offerTemplateDto, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    private OfferTemplateDto convertOfferTemplateDto(OfferTemplate4_1Dto postData) {
        OfferTemplateDto offerTemplateDto = new OfferTemplateDto();
        offerTemplateDto.setCode(postData.getCode());
        offerTemplateDto.setDescription(postData.getDescription());
        offerTemplateDto.setDisabled(postData.isDisabled());
        offerTemplateDto.setBomCode(postData.getBomCode());
        offerTemplateDto.setOfferTemplateCategoryCode(postData.getOfferTemplateCategoryCode());
        offerTemplateDto.setCustomFields(postData.getCustomFields());

        if (postData.getServiceTemplates() != null && postData.getServiceTemplates().getServiceTemplate() != null) {
            List<OfferServiceTemplateDto> offerServiceTemplateDtos = new ArrayList<>();
            for (ServiceTemplateDto st : postData.getServiceTemplates().getServiceTemplate()) {
                OfferServiceTemplateDto offerServiceTemplateDto = new OfferServiceTemplateDto();
                offerServiceTemplateDto.setMandatory(false);
                offerServiceTemplateDto.setServiceTemplate(st);
                offerServiceTemplateDtos.add(offerServiceTemplateDto);
            }
            offerTemplateDto.setOfferServiceTemplates(offerServiceTemplateDtos);
        }

        return offerTemplateDto;
    }

}
