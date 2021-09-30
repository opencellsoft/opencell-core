package org.meveo.service.payments.impl;
import java.util.Arrays;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.ws.rs.BadRequestException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.dunning.CollectionPlanStatus;
import org.meveo.service.base.PersistenceService;

/**
 * Service implementation to manage CollectionPlanStatus entity.
 * It extends {@link PersistenceService} class
 * 
 * @author Tarik
 * @version 11.0
 *
 */
@Stateless
public class CollectionPlanStatusService extends PersistenceService<CollectionPlanStatus> {

	public CollectionPlanStatus findByDunningCodeAndStatus(String dunningSettingCode, String status) {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", Arrays.asList("dunningSettings"));
        queryBuilder.addCriterion("a.dunningSettings.code", "=", dunningSettingCode, false);
        queryBuilder.addCriterion("a.status", "=", status, false);
		Query query = queryBuilder.getQuery(getEntityManager());
		try {
			return (CollectionPlanStatus) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(NonUniqueResultException e) {
			throw new BadRequestException("No unique Collection Plan Status");
		}
	}


	public CollectionPlanStatus findByDunningCode(String dunningSettingCode) {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", Arrays.asList("dunningSettings"));
        queryBuilder.addCriterion("a.dunningSettings.code", "=", dunningSettingCode, false);
		Query query = queryBuilder.getQuery(getEntityManager());
		try {
			return (CollectionPlanStatus) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(NonUniqueResultException e) {
			throw new BadRequestException("No unique Collection Plan Status");
		}
	}
}
