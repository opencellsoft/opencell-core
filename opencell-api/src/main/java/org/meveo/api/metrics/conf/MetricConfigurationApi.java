package org.meveo.api.metrics.conf;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.metric.configuration.MetricConfigurationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.metric.configuration.MetricConfiguration;
import org.meveo.service.metric.configuration.MetricConfigurationService;

/**
 *
 */
@Stateless
public class MetricConfigurationApi extends BaseCrudApi<MetricConfiguration, MetricConfigurationDto> {

    @Inject
    MetricConfigurationService metricConfigurationService;

    @Override
    public MetricConfiguration create(MetricConfigurationDto dataDto) throws MeveoApiException, BusinessException {
        validate(dataDto);

        if (metricConfigurationService.findByCode(dataDto.getCode()) != null) {
            throw new EntityAlreadyExistsException(MetricConfigurationDto.class, dataDto.getCode());
        }
        metricConfigurationService.create(MetricConfigurationDto.fromDto(dataDto));
        return metricConfigurationService.findByCode(dataDto.getCode());
    }

    @Override
    public MetricConfigurationDto find(String code) throws MeveoApiException {
        MetricConfiguration metricConfiguration = metricConfigurationService.findByCode(code);
        if (metricConfiguration == null) {
            throw new EntityDoesNotExistsException(MetricConfigurationDto.class, code);
        }
        return MetricConfigurationDto.toDto(metricConfiguration);
    }

    @Override
    public MetricConfiguration update(MetricConfigurationDto dataDto) throws MeveoApiException, BusinessException {
        validate(dataDto);

        MetricConfiguration oldMetricConfiguration = metricConfigurationService.findByCode(dataDto.getCode());
        if (oldMetricConfiguration == null) {
            throw new EntityDoesNotExistsException(MetricConfigurationDto.class, dataDto.getCode());
        }
        oldMetricConfiguration.setFullPath(dataDto.getFullPath());
        oldMetricConfiguration.setMethod(dataDto.getMethod());
        oldMetricConfiguration.setMetricType(dataDto.getMetricType());
        return metricConfigurationService.update(oldMetricConfiguration);
    }

    @Override
    public void remove(String code) {
        super.remove(code);
    }

    private void validate(MetricConfigurationDto dataDto) {
        if (StringUtils.isBlank(dataDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dataDto.getFullPath())) {
            missingParameters.add("fullPath");
        }
        if (StringUtils.isBlank(dataDto.getMethod())) {
            missingParameters.add("method");
        }
        if (StringUtils.isBlank(dataDto.getMetricType())) {
            missingParameters.add("metricType");
        }

        handleMissingParametersAndValidate(dataDto);
    }
}
