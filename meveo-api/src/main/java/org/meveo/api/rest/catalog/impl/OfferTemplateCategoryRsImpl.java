package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.catalog.OfferTemplateCategoryApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateCategoryResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.catalog.OfferTemplateCategoryRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class OfferTemplateCategoryRsImpl extends BaseRs implements OfferTemplateCategoryRs {
	
	@Inject
	private OfferTemplateCategoryApi offerTemplateCategoryApi;
	
	@Override
	public ActionStatus create(OfferTemplateCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		
		try {
			offerTemplateCategoryApi.create(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}
		
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus update(OfferTemplateCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		
		try {
			offerTemplateCategoryApi.update(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}
		
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public GetOfferTemplateCategoryResponseDto find(String offerTemplateCategoryCode) {
		GetOfferTemplateCategoryResponseDto result = new GetOfferTemplateCategoryResponseDto();
		
		try {
			result.setOfferTemplateCategory(offerTemplateCategoryApi.find(offerTemplateCategoryCode, getCurrentUser().getProvider()));
		}catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus delete(String offerTemplateCategoryCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		
		try {
			offerTemplateCategoryApi.remove(offerTemplateCategoryCode, getCurrentUser().getProvider());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}
		
		log.debug("RESPONSE={}", result);
		return result;
	}

	@Override
	public ActionStatus createOrUpdate(OfferTemplateCategoryDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		
		try {
			offerTemplateCategoryApi.createOrUpdate(postData, getCurrentUser());
		} catch (MeveoApiException e) {
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}
		
		log.debug("RESPONSE={}", result);
		return result;
	}

}
