package org.meveo.apiv2.audit.impl;

import org.meveo.apiv2.audit.AuditDataConfigurationDto;
import org.meveo.apiv2.audit.ImmutableAuditDataConfigurationDto;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.audit.AuditDataConfiguration;

public class AuditDataConfigurationMapper extends ResourceMapper<AuditDataConfigurationDto, AuditDataConfiguration> {

    @Override
    protected AuditDataConfigurationDto toResource(AuditDataConfiguration entity) {
        return ImmutableAuditDataConfigurationDto.builder().id(entity.getId()).entityClass(entity.getEntityClass()).fields(entity.getFields()).actions(entity.getActions()).build();
    }

    @Override
    protected AuditDataConfiguration toEntity(AuditDataConfigurationDto resource) {
        AuditDataConfiguration entity = new AuditDataConfiguration();
        entity.setActions(resource.getActions());
        entity.setEntityClass(resource.getEntityClass());
        entity.setFields(resource.getFields());

        return entity;
    }
}