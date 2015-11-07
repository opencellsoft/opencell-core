package org.meveo.service.custom;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.service.base.BusinessService;

/**
 * CustomEntityInstance persistence service implementation.
 * 
 */
@Stateless
public class CustomEntityInstanceService extends BusinessService<CustomEntityInstance> {

    public CustomEntityInstance findByCodeByCet(String cetCode, String code, Provider provider) {
        QueryBuilder qb = new QueryBuilder(getEntityClass(), "cei", null, provider);
        qb.addCriterion("cet.cetCode", "=", cetCode, true);
        qb.addCriterion("cet.code", "=", code, true);
        qb.addCriterionEntity("cet.provider", provider);

        try {
            return (CustomEntityInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.warn("No CustomEntityInstance by code {} and cetCode {} found", code, cetCode);
            return null;
        }
    }
}
