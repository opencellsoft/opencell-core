package org.meveo.apiv2.audit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.audit.AuditDataLog;
import org.meveo.service.audit.logging.AuditDataLogService;

public class AuditDataLogApiService implements ApiService<AuditDataLog> {

    @Inject
    private AuditDataLogService auditDataLogService;

    /**
     * Get a list of audit data logs for a given entity (class and ID). Optionally can filter only those records where the field has changed.
     * 
     * @param entityClass Entity class
     * @param entityId Entity ID
     * @param fieldName Field name
     * @return A list of audit data logs
     */
    public List<AuditDataLog> list(String entityClass, Long entityId, String userName, String fieldName) {

        Map<String, Object> filter = new HashMap<String, Object>();
        filter.put(AuditDataLogService.SEARCH_CRITERIA_ENTITY_CLASS, entityClass);
        filter.put(AuditDataLogService.SEARCH_CRITERIA_ENTITY_ID, entityId);
        if (fieldName != null) {
            filter.put(AuditDataLogService.SEARCH_CRITERIA_FIELD, fieldName);
        }

        return auditDataLogService.list(new PaginationConfiguration(filter));
    }

    /**
     * Count audit data logs matching a search criteria
     * 
     * @param filter Search criteria to apply
     * @return A number of records matched
     */
    public Long getCount(Map<String, Object> filter) {
        return auditDataLogService.count(new PaginationConfiguration(filter));
    }

    @Override
    public List<AuditDataLog> list(Long offset, Long limit, String sort, String orderBy, Map<String, Object> filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset != null ? offset.intValue() : null, limit != null ? limit.intValue() : null, filter, null, null, null, null, sort, orderBy);

        return auditDataLogService.list(paginationConfiguration);
    }

    @Override
    public Optional<AuditDataLog> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public AuditDataLog create(AuditDataLog baseEntity) {
        return null;
    }

    @Override
    public Optional<AuditDataLog> update(Long id, AuditDataLog baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<AuditDataLog> delete(Long id) {
        return Optional.empty();
    }
}