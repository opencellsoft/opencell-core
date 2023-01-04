package org.meveo.service.billing.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidInvoiceSubjectCode;
import org.meveo.service.base.PersistenceService;

@Stateless
public class UntdidInvoiceSubjectCodeService extends PersistenceService<UntdidInvoiceSubjectCode> {

    public UntdidInvoiceSubjectCode getByCode(String invoiceSubjectCode) {
        if (invoiceSubjectCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(UntdidInvoiceSubjectCode.class, "i");
        qb.addCriterion("code", "=", invoiceSubjectCode, false);

        try {
            return (UntdidInvoiceSubjectCode) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
