package org.meveo.service.billing.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.IsoIcd;
import org.meveo.model.billing.UntdidInvoiceSubjectCode;
import org.meveo.service.base.PersistenceService;

@Stateless
public class IsoIcdService extends PersistenceService<IsoIcd> {

    public IsoIcd findByCode(String isoICDCode) {
        if (isoICDCode == null) {
            return null;
        }
        QueryBuilder qb = new QueryBuilder(IsoIcd.class, "i");
        qb.addCriterion("code", "=", isoICDCode, false);

        try {
            return (IsoIcd) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<IsoIcd> getListIsoIcdByName() {
        log.debug("start of find list {} SortedByName ..", "InvoiceSubjectCode");
        QueryBuilder qb = new QueryBuilder(IsoIcd.class, "c");
        qb.addOrderCriterion("schemeName", true);
        List<IsoIcd> isoIcds = (List<IsoIcd>) qb.getQuery(getEntityManager()).getResultList();
        log.debug("start of find list {} SortedByName   result {}", new Object[] { "InvoiceSubjectCode", isoIcds == null ? "null" : isoIcds.size() });
        return isoIcds;
    }
}
