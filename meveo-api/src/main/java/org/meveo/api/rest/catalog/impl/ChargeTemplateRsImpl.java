package org.meveo.api.rest.catalog.impl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.catalog.ChargeTemplateApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.catalog.GetChargeTemplateResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.catalog.ChargeTemplateRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 **/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/catalog/chargeTemplate", tags = "charges")
public class ChargeTemplateRsImpl extends BaseRs implements ChargeTemplateRs {

	@Inject
	private ChargeTemplateApi chargeTemplateApi;

	@Override
	@ApiOperation(value = "Finds a charge template", response = GetChargeTemplateResponseDto.class)
	public GetChargeTemplateResponseDto find(@ApiParam(value = "charge template code") String chargeTemplateCode) {
		GetChargeTemplateResponseDto result = new GetChargeTemplateResponseDto();

		try {
			result.setChargeTemplate(chargeTemplateApi.find(chargeTemplateCode, getCurrentUser().getProvider()));
		} catch (MeveoApiException e) {
			result.getActionStatus().setErrorCode(e.getErrorCode());
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
