package org.meveo.service.payments.impl;

import java.util.Arrays;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Query;
import jakarta.ws.rs.BadRequestException;

import org.apache.logging.log4j.util.Strings;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.dunning.DunningAgent;
import org.meveo.service.base.PersistenceService;

/**
 * Service implementation to manage DunningSettiings entity. It extends {@link PersistenceService} class
 * 
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Stateless
public class DunningAgentService extends PersistenceService<DunningAgent> {

    public DunningAgent findByDunningCodeAndAgentEmailItem(String dunningSettingCode, String agentEmailItem) {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", Arrays.asList("dunningSettings"));
        queryBuilder.addCriterion("a.dunningSettings.code", "=", dunningSettingCode, false);
        if (!Strings.isEmpty(agentEmailItem))
            queryBuilder.addCriterion("a.agentEmailItem", "=", agentEmailItem, false);
        Query query = queryBuilder.getQuery(getEntityManager());
        try {
            return (DunningAgent) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("No unique Collection Management for dunning code : " + dunningSettingCode + " and agent email : " + agentEmailItem);
        }
    }
}
