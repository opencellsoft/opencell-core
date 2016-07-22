package org.meveo.api.rest.custom.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.custom.SearchIndexRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.admin.User;
import org.meveo.model.index.ElasticClient;


@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class SearchIndexRsImpl extends BaseRs implements SearchIndexRs {

	@Inject
	ElasticClient elasticClient;
	
	@Override
	public ActionStatus search(String types, String query) {
	     ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

	        try {
	            // Check user has <cetCode>/modify permission
	            User currentUser = getCurrentUser();
	            // FIXME
	            //if (!currentUser.hasPermission(CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode), "modify")) {
	            //    throw new LoginException("User does not have permission 'modify' on resource '" + CustomEntityTemplate.getPermissionResourceName(customEntityTemplateCode) + "'");
	            //}
	            String[] classNames = null;
	        	try {
	        	 classNames = types.split(",");
	        	} catch(Exception e){
	        		classNames = null; 
	        	}
	            result.setMessage(elasticClient.search(classNames , query, currentUser));
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