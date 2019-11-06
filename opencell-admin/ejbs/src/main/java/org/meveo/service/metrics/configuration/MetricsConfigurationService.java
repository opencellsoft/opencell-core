package org.meveo.service.metrics.configuration;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.metrics.configuration.MetricsConfiguration;
import org.meveo.service.base.BusinessService;

/**
 * Metrics configuration service implementation.
 * @author mohamed stitane
 */
@Stateless
public class MetricsConfigurationService extends BusinessService<MetricsConfiguration> {

    /**
     * Find all metrics configuration
     *
     * @return a list
     * @throws BusinessException the business exception
     */
    public List<MetricsConfiguration> findAllForCache() {
        return getEntityManager().createNamedQuery("MetricConfiguration.findAllForCache", MetricsConfiguration.class).getResultList();
    }
}
