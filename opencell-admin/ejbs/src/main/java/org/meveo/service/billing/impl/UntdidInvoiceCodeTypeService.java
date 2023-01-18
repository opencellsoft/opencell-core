package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidInvoiceCodeType;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidInvoiceCodeTypeService extends PersistenceService<UntdidInvoiceCodeType> {

    public UntdidInvoiceCodeType getByCode(String allowanceCode) {
        if (allowanceCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidInvoiceCodeType.class, "i");
        qb.addCriterion("code", "=", allowanceCode, false);

        try {
            return (UntdidInvoiceCodeType) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }    

}
