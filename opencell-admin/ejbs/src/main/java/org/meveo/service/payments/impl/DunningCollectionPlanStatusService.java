package org.meveo.service.payments.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.ws.rs.BadRequestException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.dunning.DunningCollectionPlanStatus;
import org.meveo.model.payments.DunningCollectionPlanStatusEnum;
import org.meveo.service.base.PersistenceService;

import java.util.Arrays;

/**
 * Service implementation to manage DunningCollectionPlanStatuses entity.
 * It extends {@link PersistenceService} class
 *
 * @author Mbarek-Ay
 * @version 11.0
 */
@Stateless
public class DunningCollectionPlanStatusService extends PersistenceService<DunningCollectionPlanStatus> {

    public DunningCollectionPlanStatus findByDunningCodeAndStatus(String dunningSettingCode, DunningCollectionPlanStatusEnum status) {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", Arrays.asList("dunningSettings"));
        queryBuilder.addCriterion("a.dunningSettings.code", "=", dunningSettingCode, false);
        queryBuilder.addCriterion("a.status", "=", status, false);
        Query query = queryBuilder.getQuery(getEntityManager());
        try {
            return (DunningCollectionPlanStatus) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("No unique Collection Plan Status");
        }
    }


    public DunningCollectionPlanStatus findByDunningCode(String dunningSettingCode) {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", Arrays.asList("dunningSettings"));
        queryBuilder.addCriterion("a.dunningSettings.code", "=", dunningSettingCode, false);
        Query query = queryBuilder.getQuery(getEntityManager());
        try {
            return (DunningCollectionPlanStatus) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new BadRequestException("No unique Collection Plan Status");
        }
    }

    public DunningCollectionPlanStatus findByStatus(DunningCollectionPlanStatusEnum status) {
        final DunningCollectionPlanStatus DCPstatus = getEntityManager()
                    .createNamedQuery("DunningCollectionPlanStatus.findByStatus", DunningCollectionPlanStatus.class)
                    .setParameter("status", status)
                    .setMaxResults(1)
                    .getSingleResult();
        if(DCPstatus==null) {
        	throw new BusinessException("No DunningCollectionPlanStatus found for status : "+status);
        }
		return DCPstatus;
    }
}
