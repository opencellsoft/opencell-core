package org.meveo.service.billing.impl;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.IEnable;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;

/**
 * Wallet Operation Aggregation Settings Service.
 *
 * @author khalid HORRI
 */
@Stateless
public class WalletOperationAggregationSettingsService extends PersistenceService<WalletOperationAggregationSettings> {
    /**
     * Find entities by code - wild match.
     *
     * @param code code to match
     * @return A list of entities matching code
     */
    @SuppressWarnings("unchecked")
    public WalletOperationAggregationSettings findByCode(String code) {

        QueryBuilder queryBuilder = new QueryBuilder(WalletOperationAggregationSettings.class, "a", null);
        if (IEnable.class.isAssignableFrom(WalletOperationAggregationSettings.class)) {
            queryBuilder.addBooleanCriterion("disabled", false);
        }
        queryBuilder.addCriterion("code", "=", code, true);
        return (WalletOperationAggregationSettings) queryBuilder.getQuery(getEntityManager()).getSingleResult();
    }
}

