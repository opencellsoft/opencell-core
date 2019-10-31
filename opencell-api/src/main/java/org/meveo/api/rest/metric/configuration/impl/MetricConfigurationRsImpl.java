package org.meveo.api.rest.metric.configuration.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.metric.configuration.MetricConfigurationDto;
import org.meveo.api.dto.response.GetMetricConfigurationResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.metrics.conf.MetricConfigurationApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.metric.configuration.MetricConfigurationRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class MetricConfigurationRsImpl extends BaseRs implements MetricConfigurationRs {

    @Inject
    MetricConfigurationApi metricConfigurationApi;

    @Override
    public ActionStatus create(MetricConfigurationDto metricConfigurationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            metricConfigurationApi.create(metricConfigurationDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetMetricConfigurationResponse find(String code) {
        GetMetricConfigurationResponse result = new GetMetricConfigurationResponse();

        try {
            result.setMetricConfigurationDto(metricConfigurationApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus update(MetricConfigurationDto metricConfigurationDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            metricConfigurationApi.update(metricConfigurationDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            metricConfigurationApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
