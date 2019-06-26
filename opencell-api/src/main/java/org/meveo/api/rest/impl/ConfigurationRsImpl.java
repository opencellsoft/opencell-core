package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.ConfigurationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.GetConfigurationResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.ConfigurationRs;

/** 
 * @author Edward P. Legaspi
 * @author Khalid HORRI
 * @lastModifiedVersion 7.1
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ConfigurationRsImpl extends BaseRs implements ConfigurationRs {
	
	@Inject
	private ConfigurationApi configurationApi;

	@Override
	public GetConfigurationResponse systemProperties() {
        GetConfigurationResponse result = new GetConfigurationResponse();
        try {
            result.setProperties(configurationApi.getProperties());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
	}

}
