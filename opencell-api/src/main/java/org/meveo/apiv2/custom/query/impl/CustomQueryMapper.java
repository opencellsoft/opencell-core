package org.meveo.apiv2.custom.query.impl;

import static org.meveo.apiv2.custom.ImmutableCustomQuery.builder;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.custom.ImmutableCustomQuery;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.custom.query.CustomQuery;

public class CustomQueryMapper extends ResourceMapper<org.meveo.apiv2.custom.CustomQuery, CustomQuery> {

    @Override
    protected org.meveo.apiv2.custom.CustomQuery toResource(CustomQuery entity) {
        try {
            ImmutableCustomQuery resource = builder()
                    .code(entity.getCode())
                    .description(entity.getDescription())
                    .targetEntity(entity.getTargetEntity())
                    .visibility(entity.getVisibility())
                    .fields(entity.getFields())
                    .filters(entity.getFilters())
                    .generatedQuery(entity.getGeneratedQuery())
                    .build();
            return builder()
                    .from(resource)
                    .id(entity.getId())
                    .build();
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    @Override
    protected CustomQuery toEntity(org.meveo.apiv2.custom.CustomQuery resource) {
        return null;
    }
}