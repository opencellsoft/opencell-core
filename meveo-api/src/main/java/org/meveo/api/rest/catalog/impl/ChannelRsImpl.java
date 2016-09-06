package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.catalog.ChannelApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.response.catalog.GetChannelResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.ChannelRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ChannelRsImpl extends BaseRs implements ChannelRs {

    @Inject
    private ChannelApi channelApi;

	@Override
	public ActionStatus create(ChannelDto postData) {
	        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	        	channelApi.create(postData, getCurrentUser());
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
	public ActionStatus update(ChannelDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            channelApi.update(postData, getCurrentUser());
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
	public GetChannelResponseDto find(String channelCode) {
		GetChannelResponseDto result = new GetChannelResponseDto();

	        try {
	            result.setChannel(channelApi.find(channelCode, getCurrentUser().getProvider()));
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
	public ActionStatus delete(String channelCode) {
		 ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	            channelApi.remove(channelCode, getCurrentUser());
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
	public ActionStatus createOrUpdate(ChannelDto postData) {
		 ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	            channelApi.createOrUpdate(postData, getCurrentUser());
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


}
