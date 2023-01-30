package org.meveo.service.billing.impl;

import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidVatex;
import org.meveo.service.base.PersistenceService;

public class UntdidVatexService extends PersistenceService<UntdidVatex> {
    public UntdidVatex getByCode(String byCode) {
        if (byCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidVatex.class, "i");
        qb.addCriterion("code", "=", byCode, false);

        try {
            return (UntdidVatex) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}