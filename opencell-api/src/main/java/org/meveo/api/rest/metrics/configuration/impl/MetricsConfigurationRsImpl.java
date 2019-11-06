package org.meveo.api.rest.metrics.configuration.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.metrics.configuration.MetricsConfigurationDto;
import org.meveo.api.dto.response.GetMetricsConfigurationResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.metrics.conf.MetricsConfigurationApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.metrics.configuration.MetricsConfigurationRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class MetricsConfigurationRsImpl extends BaseRs implements MetricsConfigurationRs {

    @Inject
    MetricsConfigurationApi metricsConfigurationApi;

    @Override
    public ActionStatus create(MetricsConfigurationDto metricsConfigurationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            metricsConfigurationApi.create(metricsConfigurationDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetMetricsConfigurationResponse find(String code) {
        GetMetricsConfigurationResponse result = new GetMetricsConfigurationResponse();

        try {
            result.setMetricsConfigurationDto(metricsConfigurationApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus update(MetricsConfigurationDto metricsConfigurationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            metricsConfigurationApi.update(metricsConfigurationDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            metricsConfigurationApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
