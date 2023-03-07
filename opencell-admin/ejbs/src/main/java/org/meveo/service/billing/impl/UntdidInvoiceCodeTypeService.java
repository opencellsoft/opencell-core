package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.UntdidInvoiceCodeType;
import org.meveo.model.billing.UntdidInvoiceSubjectCode;
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
    
    @SuppressWarnings("unchecked")
    public List<UntdidInvoiceCodeType> getListInvoiceCodeTypeByName() {
        log.debug("start of find list {} SortedByName ..", "InvoiceCodeType");
        QueryBuilder qb = new QueryBuilder(UntdidInvoiceSubjectCode.class, "c");
        qb.addOrderCriterion("name", true);
        List<UntdidInvoiceCodeType> invoiceCodeTypes = (List<UntdidInvoiceCodeType>) qb.getQuery(getEntityManager()).getResultList();
        log.debug("start of find list {} SortedByName   result {}", new Object[] { "InvoiceCodeType", invoiceCodeTypes == null ? "null" : invoiceCodeTypes.size() });
        return invoiceCodeTypes;
    }

}
