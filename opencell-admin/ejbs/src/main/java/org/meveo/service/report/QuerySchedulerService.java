package org.meveo.service.report;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.model.report.query.QueryScheduler;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.service.base.BusinessService;

@Stateless
public class QuerySchedulerService extends BusinessService<QueryScheduler> {

    /**
     * findByReportQuery
     * 
     * @param reportQuery
     * @return
     */
    public QueryScheduler findByReportQuery(ReportQuery reportQuery) {
        try {
            TypedQuery<QueryScheduler> q = getEntityManager().createNamedQuery("QueryScheduler.findByReportQuery", QueryScheduler.class).setParameter("reportQuery", reportQuery);
            return q.getSingleResult();
        } catch (NoResultException e) {
            log.warn("failed to findByReportQuery", e);
            return null;
        }
    }
}