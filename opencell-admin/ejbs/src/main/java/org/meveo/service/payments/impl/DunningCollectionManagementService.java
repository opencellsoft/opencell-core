package org.meveo.service.payments.impl;
import java.util.Arrays;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.ws.rs.BadRequestException;

import org.apache.logging.log4j.util.Strings;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.dunning.DunningCollectionManagement;
import org.meveo.service.base.PersistenceService;

/**
 * Service implementation to manage DunningSettiings entity.
 * It extends {@link PersistenceService} class
 * 
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Stateless
public class DunningCollectionManagementService extends PersistenceService<DunningCollectionManagement> {

	
	public DunningCollectionManagement findByDunningCodeAndAgentEmailItem(String dunningSettingCode, String agentEmailItem) {
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", Arrays.asList("dunningSettings"));
		queryBuilder.addCriterion("a.dunningSettings.code", "=", dunningSettingCode, false);
		if(!Strings.isEmpty(agentEmailItem))
			queryBuilder.addCriterion("a.agentEmailItem", "=", agentEmailItem, false);
		Query query = queryBuilder.getQuery(getEntityManager());
		try {
			return (DunningCollectionManagement) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(NonUniqueResultException e) {
			throw new BadRequestException("No unique Collection Management for dunning code : " + dunningSettingCode  + " and agent email : " + agentEmailItem);
		}
	}
}
