package org.meveo.api.metrics.conf;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.metrics.configuration.MetricsConfigurationDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.cache.MetricsConfigurationCacheContainerProvider;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.metrics.configuration.MetricsConfiguration;
import org.meveo.service.metrics.configuration.MetricsConfigurationService;

import static javax.ws.rs.HttpMethod.DELETE;
import static javax.ws.rs.HttpMethod.GET;
import static javax.ws.rs.HttpMethod.HEAD;
import static javax.ws.rs.HttpMethod.OPTIONS;
import static javax.ws.rs.HttpMethod.PATCH;
import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.HttpMethod.PUT;

/**
 *
 */
@Stateless
public class MetricsConfigurationApi extends BaseCrudApi<MetricsConfiguration, MetricsConfigurationDto> {

    @Inject
    MetricsConfigurationService metricsConfigurationService;

    @Inject
    MetricsConfigurationCacheContainerProvider metricsConfigurationCacheContainerProvider;

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @Override
    public MetricsConfiguration create(MetricsConfigurationDto dataDto) {
        validate(dataDto);

        if (metricsConfigurationService.findByCode(dataDto.getCode()) != null) {
            throw new EntityAlreadyExistsException(MetricsConfigurationDto.class, dataDto.getCode());
        }
        metricsConfigurationService.create(MetricsConfigurationDto.fromDto(dataDto));
        // reset cache
        metricsConfigurationCacheContainerProvider.clearAndUpdateCache();
        return metricsConfigurationService.findByCode(dataDto.getCode());
    }

    @Override
    public MetricsConfigurationDto find(String code) {
        MetricsConfiguration metricsConfiguration = metricsConfigurationService.findByCode(code);
        if (metricsConfiguration == null) {
            throw new EntityDoesNotExistsException(MetricsConfigurationDto.class, code);
        }
        return MetricsConfigurationDto.toDto(metricsConfiguration);
    }

    @Override
    public MetricsConfiguration update(MetricsConfigurationDto dataDto) {
        validate(dataDto);

        MetricsConfiguration oldMetricsConfiguration = metricsConfigurationService.findByCode(dataDto.getCode());
        if (oldMetricsConfiguration == null) {
            throw new EntityDoesNotExistsException(MetricsConfigurationDto.class, dataDto.getCode());
        }
        oldMetricsConfiguration.setFullPath(dataDto.getFullPath());
        oldMetricsConfiguration.setMethod(dataDto.getMethod());
        oldMetricsConfiguration.setMetricsType(dataDto.getMetricsType());
        MetricsConfiguration updatedMetric = metricsConfigurationService.update(oldMetricsConfiguration);
        // reset cache
        metricsConfigurationCacheContainerProvider.clearAndUpdateCache();

        return updatedMetric;
    }

    @Override
    public void remove(String code) {
        MetricsConfiguration oldMetricsConfiguration = metricsConfigurationService.findByCode(code);
        if (oldMetricsConfiguration == null) {
            throw new EntityDoesNotExistsException(MetricsConfigurationDto.class, code);
        }
        metricsConfigurationService.remove(oldMetricsConfiguration.getId());
        // reset cache
        metricsConfigurationCacheContainerProvider.clearAndUpdateCache();

        // remove metric history
        String name = oldMetricsConfiguration.getFullPath().replace("/", "_");
        log.debug("Removed metrics {}", name);
        if ("*.jsf".equals(name)) {
            registry.removeMatching((metricsID, metrics) -> metricsID.getName().endsWith("jsf"));
        } else {
            registry.removeMatching((metricsID, metrics) -> metricsID.getName().contains(name));
        }
    }

    private void validate(MetricsConfigurationDto dataDto) {
        if (StringUtils.isBlank(dataDto.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(dataDto.getFullPath())) {
            missingParameters.add("fullPath");
        }
        if (StringUtils.isBlank(dataDto.getMethod())) {
            missingParameters.add("method");
        }
        if (StringUtils.isBlank(dataDto.getMetricsType())) {
            missingParameters.add("metricType");
        }
        List<String> metrics = Arrays.asList("counter", "timer", "gauge", "histogram", "meter");
        if (!metrics.contains(dataDto.getMetricsType())) {
            throw new InvalidParameterException(" Invalid metrics type " + dataDto.getMetricsType() + ", allowed metrics values : " + metrics + " ");
        }
        List<String> methods = Arrays.asList(DELETE, GET, POST, PUT, PATCH, HEAD, OPTIONS);
        if (!methods.contains(dataDto.getMethod())) {
            throw new InvalidParameterException(" Invalid request method " + dataDto.getMethod() + ", allowed methods values : " + methods + " ");
        }
        if ("timer".equals(dataDto.getMetricsType())) {
            // validate metrics unit when metrics type is timer
            List<String> fields = Stream.of(MetricUnits.class.getDeclaredFields())
                    .map(Field::getName)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            if (!fields.contains(dataDto.getMetricsUnit())) {
                throw new InvalidParameterException(" Invalid metrics unit " + dataDto.getMetricsUnit() + ", allowed metrics unit values : " + fields + " ");
            }
        }

        handleMissingParametersAndValidate(dataDto);
    }
}
