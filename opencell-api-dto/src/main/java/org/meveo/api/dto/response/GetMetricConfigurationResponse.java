package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.metric.configuration.MetricConfigurationDto;

/**
 * The Class GetMetricConfigurationResponse.
 *
 * @author mohamed STITANE
 */
@XmlRootElement(name = "GetMetricConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetMetricConfigurationResponse extends BaseResponse {
    /** The metric configuration dto. */
    private MetricConfigurationDto metricConfigurationDto;

    /**
     * Instantiates a new gets the metric configuration response.
     */
    public GetMetricConfigurationResponse() {
    }

    public MetricConfigurationDto getMetricConfigurationDto() {
        return metricConfigurationDto;
    }

    public void setMetricConfigurationDto(MetricConfigurationDto metricConfigurationDto) {
        this.metricConfigurationDto = metricConfigurationDto;
    }

    @Override
    public String toString() {
        return "GetMetricConfigurationResponse [" + "metricConfigurationDto=" + metricConfigurationDto + ']';
    }
}
