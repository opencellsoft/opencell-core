package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.ConfigurationApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.ConfigurationRs;

/** 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ConfigurationRsImpl extends BaseRs implements ConfigurationRs {
	
	@Inject
	private ConfigurationApi configurationApi;

	@Override
	public ActionStatus systemProperties() {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            result.setMessage(configurationApi.getPropertiesAsJsonString());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
	}

}
