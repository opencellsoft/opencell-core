package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidAllowanceCode;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidAllowanceCodeService extends PersistenceService<UntdidAllowanceCode> {

    public UntdidAllowanceCode getByCode(String allowanceCode) {
        if (allowanceCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidAllowanceCode.class, "i");
        qb.addCriterion("code", "=", allowanceCode, false);

        try {
            return (UntdidAllowanceCode) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
