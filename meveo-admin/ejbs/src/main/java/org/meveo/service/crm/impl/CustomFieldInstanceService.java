package org.meveo.service.crm.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

@Stateless
public class CustomFieldInstanceService extends PersistenceService<CustomFieldInstance> {

    /**
     * Get a list of custom field instances to populate a cache
     * 
     * @return A list of custom field instances
     */
    public List<CustomFieldInstance> getCFIForCache() {

        List<CustomFieldInstance> results = new ArrayList<CustomFieldInstance>();
        String[] queryNames = { "CustomFieldInstance.getCFIForCacheAccount", "CustomFieldInstance.getCFIForCacheProvider", "CustomFieldInstance.getCFIForCacheSubscription",
                "CustomFieldInstance.getCFIForCacheCharge", "CustomFieldInstance.getCFIForCacheService", "CustomFieldInstance.getCFIForCacheOffer",
                "CustomFieldInstance.getCFIForCacheAccess", "CustomFieldInstance.getCFIForCacheJobInstance", };

        for (String queryName : queryNames) {
            results.addAll(getEntityManager().createNamedQuery(queryName, CustomFieldInstance.class).getResultList());
        }
        return results;
    }

    // /**
    // * Convert BusinessEntityWrapper to an entity by doing a lookup in DB
    // *
    // * @param businessEntityWrapper Business entity information
    // * @return A BusinessEntity object
    // */
    // @SuppressWarnings("unchecked")
    // public BusinessEntity convertToBusinessEntityFromCfV(EntityReferenceWrapper businessEntityWrapper, Provider provider) {
    // if (businessEntityWrapper == null) {
    // return null;
    // }
    // Query query = getEntityManager().createQuery("select e from " + businessEntityWrapper.getClassname() + " e where e.code=:code and e.provider=:provider");
    // query.setParameter("code", businessEntityWrapper.getCode());
    // query.setParameter("provider", provider);
    // List<BusinessEntity> entities = query.getResultList();
    // if (entities.size() > 0) {
    // return entities.get(0);
    // } else {
    // return null;
    // }
    // }

    @SuppressWarnings("unchecked")
    public List<BusinessEntity> findBusinessEntityForCFVByCode(String className, String wildcode, Provider provider) {
        Query query = getEntityManager().createQuery("select e from " + className + " e where lower(e.code) like :code and e.provider=:provider");
        query.setParameter("code", "%" + wildcode.toLowerCase() + "%");
        query.setParameter("provider", provider);
        List<BusinessEntity> entities = query.getResultList();
        return entities;
    }
    
	@SuppressWarnings("unchecked")
	public List<CustomFieldInstance> findByAccount(AccountEntity account, Provider provider) {
		QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c", null, provider);
		qb.addCriterionEntity("account", account);

		try {
			return qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}
    
}
