package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidPaymentMeans;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidPaymentMeansService extends PersistenceService<UntdidPaymentMeans> {

    public UntdidPaymentMeans getByCode(String untdidPayment) {
        if (untdidPayment == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidPaymentMeans.class, "i");
        qb.addCriterion("code", "=", untdidPayment, false);

        try {
            return (UntdidPaymentMeans) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
