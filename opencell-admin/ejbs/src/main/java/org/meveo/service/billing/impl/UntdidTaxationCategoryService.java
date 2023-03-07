package org.meveo.service.billing.impl;

import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidTaxationCategory;
import org.meveo.service.base.PersistenceService;

public class UntdidTaxationCategoryService extends PersistenceService<UntdidTaxationCategory> {
    public UntdidTaxationCategory getByCode(String byCode) {
        if (byCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidTaxationCategory.class, "i");
        qb.addCriterion("code", "=", byCode, false);

        try {
            return (UntdidTaxationCategory) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
