package org.meveo.api.dto.metric.configuration;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.BusinessEntity;
import org.meveo.model.metric.configuration.MetricConfiguration;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MetricConfigurationDto  extends BusinessEntityDto {

    /** The full path. */
    @XmlElement(required = true)
    private String fullPath;

    /** The request method. */
    @XmlElement(required = true)
    private String method;

    /** The metric type. */
    @XmlElement(required = true)
    private String metricType;

    /**
     * Instantiate a new MetricConfiguration Dto
     */
    public MetricConfigurationDto() {
    }

    public MetricConfigurationDto(String fullPath, String method, String metricType) {
        this.fullPath = fullPath;
        this.method = method;
        this.metricType = metricType;
    }

    public MetricConfigurationDto(BusinessEntity e, String fullPath, String method, String metricType) {
        super(e);
        this.fullPath = fullPath;
        this.method = method;
        this.metricType = metricType;
    }

    /**
     * Convert MetricConfiguration entity to DTO
     * @param entity an entity
     * @return a dto
     */
    public static MetricConfigurationDto toDto(MetricConfiguration entity) {
        MetricConfigurationDto dto = new MetricConfigurationDto();
        dto.setFullPath(entity.getFullPath());
        dto.setMethod(Optional.of(entity.getMethod()).map(String::toUpperCase).orElse(""));
        dto.setMetricType(Optional.of(entity.getMetricType()).map(String::toLowerCase).orElse(""));
        dto.setCode(entity.getCode());
        dto.setDescription(entity.getDescription());

        return dto;
    }
    /**
     * Convert DTO to MetricConfiguration entity
     * @param metricConfigurationDto a metric configuration dto
     * @return an entity
     */
    public static MetricConfiguration fromDto(MetricConfigurationDto metricConfigurationDto) {
        MetricConfiguration entity = new MetricConfiguration();
        entity.setFullPath(metricConfigurationDto.getFullPath());
        entity.setMethod(metricConfigurationDto.getMethod());
        entity.setMetricType(metricConfigurationDto.getMetricType());
        entity.setCode(metricConfigurationDto.getCode());
        entity.setDescription(metricConfigurationDto.getDescription());
        return entity;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }
}
