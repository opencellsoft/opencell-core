package org.meveo.service.metric.configuration;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.metric.configuration.MetricConfiguration;
import org.meveo.service.base.BusinessService;

/**
 * Title service implementation.
 * @author mohamed stitane
 */
@Stateless
public class MetricConfigurationService extends BusinessService<MetricConfiguration> {

    /**
     * Find all metric configuration
     *
     * @return a list
     * @throws BusinessException the business exception
     */
    public List<MetricConfiguration> findAllForCache() throws BusinessException {
        return getEntityManager().createNamedQuery("MetricConfiguration.findAllForCache", MetricConfiguration.class).getResultList();
    }
}
