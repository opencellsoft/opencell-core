package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidInvoiceSubjectCode;
import org.meveo.model.payments.OCCTemplate;
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

    @SuppressWarnings("unchecked")
    public List<UntdidInvoiceSubjectCode> getListInvoiceSubjectCodeByName() {
        log.debug("start of find list {} SortedByName ..", "InvoiceSubjectCode");
        QueryBuilder qb = new QueryBuilder(UntdidInvoiceSubjectCode.class, "c");
        qb.addOrderCriterion("codeName", true);
        List<UntdidInvoiceSubjectCode> invoiceSubjectCodes = (List<UntdidInvoiceSubjectCode>) qb.getQuery(getEntityManager()).getResultList();
        log.debug("start of find list {} SortedByName   result {}", new Object[] { "InvoiceSubjectCode", invoiceSubjectCodes == null ? "null" : invoiceSubjectCodes.size() });
        return invoiceSubjectCodes;
    }
}
