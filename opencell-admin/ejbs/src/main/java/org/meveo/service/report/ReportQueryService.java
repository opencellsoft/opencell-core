package org.meveo.service.report;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.report.query.ReportQuery;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ReportQueryService extends BusinessService<ReportQuery> {

    /**
     *
     * @param configuration : filtering & pagination configuration used by the query
     * @param userName : current user
     * @return list of ReportQueries
     */
    public List<ReportQuery> reportQueriesAllowedForUser(PaginationConfiguration configuration, String userName) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("SQL", "visibility = 'PRIVATE' OR visibility = 'PUBLIC' OR visibility = 'PROTECTED'");
        filters.put("auditable.creator", userName);
        configuration.setFilters(filters);
        return list(configuration);
    }
}