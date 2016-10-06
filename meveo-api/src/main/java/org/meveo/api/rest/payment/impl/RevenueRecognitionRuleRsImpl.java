package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtoResponse;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtosResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.RevenueRecognitionRuleApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.RevenueRecognitionRulesRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class RevenueRecognitionRuleRsImpl extends BaseRs implements RevenueRecognitionRulesRs{

	@Inject
	RevenueRecognitionRuleApi revenueRecognitionRuleApi;
	
	@Override
	public ActionStatus create(RevenueRecognitionRuleDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	revenueRecognitionRuleApi.create(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error occurred while creating revenue recognition rule ", e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error generated while creating revenue recognition rule ", e);
        }

        return result;
	}

	@Override
	public ActionStatus update(RevenueRecognitionRuleDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	revenueRecognitionRuleApi.update(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error occurred while updating revenue recognition rule ", e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error generated while updating revenue recognition rule ", e);
        }

        return result;
	}

	@Override
	public ActionStatus createOrUpdate(RevenueRecognitionRuleDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	revenueRecognitionRuleApi.createOrUpdate(postData, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error occurred while creating or updating revenue recognition rule ", e);
        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
            log.error("error generated while creating or updating revenue recognition rule ", e);
        }

        return result;
	}

	@Override
	public RevenueRecognitionRuleDtoResponse find(String revenueRecognitionRuleCode) {
		RevenueRecognitionRuleDtoResponse result = new RevenueRecognitionRuleDtoResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
		try {
			result.setRevenueRecognitionRuleDto(revenueRecognitionRuleApi.find(revenueRecognitionRuleCode, getCurrentUser()));
		} catch (MeveoApiException | BusinessException e) {
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
            log.error("Error when find a RevenueRecognitionRule by code {}", revenueRecognitionRuleCode, e);
		}
		return result;
	}

	@Override
	public ActionStatus remove(String revenueRecognitionRuleCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			revenueRecognitionRuleApi.remove(revenueRecognitionRuleCode, getCurrentUser());
			
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }
		return result;
	}

	@Override
	public RevenueRecognitionRuleDtosResponse list() {
		RevenueRecognitionRuleDtosResponse result=new RevenueRecognitionRuleDtosResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
			result.setRevenueRecognitionRules(revenueRecognitionRuleApi.list(getCurrentUser()));
        } catch (Exception e) {
            log.error("Failed to execute API", e);
            result.getActionStatus().setErrorCode(e instanceof BusinessException ? MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION : MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }
		return result;
	}


}
