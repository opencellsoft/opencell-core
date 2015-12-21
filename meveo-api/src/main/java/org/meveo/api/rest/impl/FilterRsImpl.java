package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.FilterApi;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.FilterRs;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Tyshan Shi
 *
**/
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
@Api(value = "/filter", tags = "filter")
public class FilterRsImpl extends BaseRs implements FilterRs {
	@Inject
	private FilterApi filterApi;

	@Override
	@ApiOperation(value = "This function create or update a filter", response = ActionStatus.class)
	public ActionStatus createOrUpdate(FilterDto dto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			filterApi.createOrUpdate(dto, this.getCurrentUser());
		}catch(MeveoApiException e){
			result.setErrorCode(e.getErrorCode());
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setErrorCode(MeveoApiErrorCode.GENERIC_API_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
