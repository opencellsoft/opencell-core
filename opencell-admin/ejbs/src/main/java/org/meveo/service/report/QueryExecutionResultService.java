package org.meveo.service.report;

import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.model.report.query.QueryExecutionResult;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.service.base.PersistenceService;

@Stateless
public class QueryExecutionResultService extends PersistenceService<QueryExecutionResult> {
    /**
     * findByReportQuery
     *
     * @param reportQuery
     * @return
     */
    public List<Long> findByReportQuery(ReportQuery reportQuery) {
        try {
            TypedQuery<Long> q = getEntityManager().createNamedQuery("QueryExecutionResult.findIdsByReportQuery", Long.class)
                    .setParameter("reportQuery", reportQuery);
            return q.getResultList();
        } catch (NoResultException e) {
            log.warn("failed to findByReportQuery", e);
            return Collections.emptyList();
        }
    }
}
