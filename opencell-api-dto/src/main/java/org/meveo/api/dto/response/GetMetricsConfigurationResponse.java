package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.metrics.configuration.MetricsConfigurationDto;

/**
 * The Class GetMetricConfigurationResponse.
 *
 * @author mohamed STITANE
 */
@XmlRootElement(name = "GetMetricConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetMetricsConfigurationResponse extends BaseResponse {
    /** The metrics configuration dto. */
    private MetricsConfigurationDto metricsConfigurationDto;

    public MetricsConfigurationDto getMetricsConfigurationDto() {
        return metricsConfigurationDto;
    }

    public void setMetricsConfigurationDto(MetricsConfigurationDto metricsConfigurationDto) {
        this.metricsConfigurationDto = metricsConfigurationDto;
    }

    @Override
    public String toString() {
        return "GetMetricConfigurationResponse [" + "metricConfigurationDto=" + metricsConfigurationDto + ']';
    }
}
