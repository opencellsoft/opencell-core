package org.meveo.apiv2.audit.service;

import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.audit.AuditDataConfiguration;
import org.meveo.service.audit.AuditDataConfigurationService;

import software.amazon.awssdk.utils.StringUtils;

public class AuditDataConfigurationApiService implements ApiService<AuditDataConfiguration> {

    @Inject
    private AuditDataConfigurationService auditDataConfigurationService;

    @Override
    public AuditDataConfiguration create(AuditDataConfiguration auditDataConfiguration) {
        AuditDataConfiguration entity = auditDataConfigurationService.findByEntityClass(auditDataConfiguration.getEntityClass());
        if (entity != null) {
            throw new EntityAlreadyExistsException(AuditDataConfiguration.class, auditDataConfiguration.getEntityClass());
        }

        auditDataConfigurationService.create(auditDataConfiguration);
        return auditDataConfiguration;
    }

    @Override
    public List<AuditDataConfiguration> list(Long offset, Long limit, String sort, String orderBy, Map<String, Object> filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset != null ? offset.intValue() : null, limit != null ? limit.intValue() : null, filter, null, null, null, null, sort, orderBy);
        return auditDataConfigurationService.list(paginationConfiguration);
    }

    @Override
    public Long getCount(String filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, null, filter, null, null, null);
        return auditDataConfigurationService.count(paginationConfiguration);
    }

    @Override
    public Long getCount(Map<String, Object> filter) {
        PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null, filter, null, null, null, null);
        return auditDataConfigurationService.count(paginationConfiguration);
    }

    @Override
    public Optional<AuditDataConfiguration> findById(Long id) {
        return ofNullable(auditDataConfigurationService.findById(id));
    }

    @Override
    public Optional<AuditDataConfiguration> update(Long id, AuditDataConfiguration baseEntity) {
        Optional<AuditDataConfiguration> auditDataConfigurationOptional = findById(id);
        if (auditDataConfigurationOptional.isEmpty()) {
            return Optional.empty();
        }
        AuditDataConfiguration entity = auditDataConfigurationOptional.get();

        if (baseEntity.getEntityClass() != null) {
            entity.setEntityClass(baseEntity.getEntityClass());
        }

        if (baseEntity.getFields() != null) {
            if (StringUtils.isBlank(baseEntity.getFields())) {
                entity.setFields(null);
            } else {
                entity.setFields(baseEntity.getFields());
            }
        }

        if (baseEntity.getActions() != null) {
            if (StringUtils.isBlank(baseEntity.getActions())) {
                entity.setActions(null);
            } else {
                entity.setActions(baseEntity.getActions());
            }
        }

        auditDataConfigurationService.update(entity);

        return ofNullable(entity);
    }

    public Optional<AuditDataConfiguration> findByEntityClass(String entityClass) {
        AuditDataConfiguration auditDataConfig = auditDataConfigurationService.findByEntityClass(entityClass);
        if (auditDataConfig == null) {
            throw new BadRequestException("No Audit data configuration found for entity class: " + entityClass);
        }
        return ofNullable(auditDataConfig);
    }

    @Override
    public Optional<AuditDataConfiguration> delete(Long id) {
        Optional<AuditDataConfiguration> auditDataConfig = findById(id);
        if (auditDataConfig.isPresent()) {
            try {
                auditDataConfigurationService.remove(auditDataConfig.get());
            } catch (Exception e) {
                if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                    throw new DeleteReferencedEntityException(AuditDataConfiguration.class, id);
                }
                throw new BadRequestException(e);
            }
        }
        return auditDataConfig;
    }

    public Optional<AuditDataConfiguration> delete(String entityClass) {
        Optional<AuditDataConfiguration> auditDataConfig = findByEntityClass(entityClass);
        if (auditDataConfig.isPresent()) {
            try {
                auditDataConfigurationService.remove(auditDataConfig.get());
                auditDataConfigurationService.commit();
            } catch (Exception e) {
                if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                    throw new DeleteReferencedEntityException(AuditDataConfiguration.class, entityClass);
                }
                throw new BadRequestException(e);
            }
        }
        return auditDataConfig;
    }
}