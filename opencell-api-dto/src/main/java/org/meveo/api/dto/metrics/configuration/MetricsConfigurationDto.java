/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.metrics.configuration;

import java.util.Optional;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.metrics.configuration.MetricsConfiguration;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MetricsConfigurationDto extends BusinessEntityDto {

    /** The full path. */
    @XmlElement(required = true)
    private String fullPath;

    /** The request method. */
    @XmlElement(required = true)
    private String method;

    /** The metrics type. */
    @XmlElement(required = true)
    private String metricsType;

    /** The metrics unit . */
    @XmlElement(required = true)
    private String metricsUnit;

    /**
     * Convert MetricConfiguration entity to DTO
     * @param entity an entity
     * @return a dto
     */
    public static MetricsConfigurationDto toDto(MetricsConfiguration entity) {
        MetricsConfigurationDto dto = new MetricsConfigurationDto();
        dto.setFullPath(entity.getFullPath());
        dto.setMethod(Optional.of(entity.getMethod()).map(String::toUpperCase).orElse(""));
        dto.setMetricsType(Optional.of(entity.getMetricsType()).map(String::toLowerCase).orElse(""));
        dto.setMetricsUnit(Optional.of(entity.getMetricsUnit()).map(String::toLowerCase).orElse(""));
        dto.setCode(entity.getCode());
        dto.setDescription(entity.getDescription());

        return dto;
    }
    /**
     * Convert DTO to MetricsConfiguration entity
     * @param metricsConfigurationDto a metrics configuration dto
     * @return an entity
     */
    public static MetricsConfiguration fromDto(MetricsConfigurationDto metricsConfigurationDto) {
        MetricsConfiguration entity = new MetricsConfiguration();
        entity.setFullPath(metricsConfigurationDto.getFullPath());
        entity.setMethod(metricsConfigurationDto.getMethod());
        entity.setMetricsType(metricsConfigurationDto.getMetricsType());
        entity.setMetricsUnit(metricsConfigurationDto.getMetricsUnit());
        entity.setCode(metricsConfigurationDto.getCode());
        entity.setDescription(metricsConfigurationDto.getDescription());
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

    public String getMetricsType() {
        return metricsType;
    }

    public void setMetricsType(String metricsType) {
        this.metricsType = metricsType;
    }

    public String getMetricsUnit() {
        return metricsUnit;
    }

    public void setMetricsUnit(String metricsUnit) {
        this.metricsUnit = metricsUnit;
    }
}
