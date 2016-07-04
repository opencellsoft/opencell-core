package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.filter.FilteredListDto;
import org.meveo.api.dto.response.billing.FilteredListResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.filter.FilteredListApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.FilteredListWs;
import org.slf4j.Logger;

@WebService(serviceName = "FilteredListWs", endpointInterface = "org.meveo.api.ws.FilteredListWs")
@Interceptors({ WsRestApiInterceptor.class })
public class FilteredListWsImpl extends BaseWs implements FilteredListWs {

    @Inject
    private Logger log;

    @Inject
    private FilteredListApi filteredListApi;

    @Override
    public FilteredListResponseDto list(String filter, Integer firstRow, Integer numberOfRows) {
        FilteredListResponseDto result = new FilteredListResponseDto();
        try {
            String response = filteredListApi.list(filter, firstRow, numberOfRows, getCurrentUser());
            result.getActionStatus().setMessage(response);
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
    public FilteredListResponseDto listByXmlInput(FilteredListDto postData) {
        FilteredListResponseDto result = new FilteredListResponseDto();
        try {
            String response = filteredListApi.listByXmlInput(postData, getCurrentUser());
            result.getActionStatus().setMessage(response);
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
    
    public FilteredListResponseDto search(String[] classnames,String query){
    	 FilteredListResponseDto result = new FilteredListResponseDto();
         try {
             String response = filteredListApi.search(classnames, query, getCurrentUser());
             result.getActionStatus().setMessage(response);
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

}
