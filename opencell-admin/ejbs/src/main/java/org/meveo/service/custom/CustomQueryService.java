package org.meveo.service.custom;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.custom.query.CustomQuery;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class CustomQueryService extends BusinessService<CustomQuery> {

    public List<CustomQuery> customQueriesAllowedForUser(PaginationConfiguration configuration, String userName) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("SQL", "visibility = 'PRIVATE' OR visibility = 'PUBLIC' OR visibility = 'PROTECTED'");
        filters.put("auditable.creator", userName);
        configuration.setFilters(filters);
        return list(configuration);
    }
}