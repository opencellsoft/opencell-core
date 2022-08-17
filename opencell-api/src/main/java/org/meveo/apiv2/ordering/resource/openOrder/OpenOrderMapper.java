package org.meveo.apiv2.ordering.resource.openOrder;

import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.apiv2.ordering.resource.oo.ImmutableOpenOrderDto;
import org.meveo.apiv2.ordering.resource.oo.OpenOrderDto;
import org.meveo.apiv2.ordering.resource.openOrderTemplate.ThresholdMapper;
import org.meveo.model.ordering.OpenOrder;

import java.util.stream.Collectors;

public class OpenOrderMapper extends ResourceMapper<OpenOrderDto, OpenOrder> {


    private ThresholdMapper thresholdMapper = new ThresholdMapper();

    @Override
    public OpenOrderDto toResource(OpenOrder entity) {

        return  ImmutableOpenOrderDto.builder()
                .id(entity.getId())
                .externalReference(entity.getExternalReference())
                .thresholds(thresholdMapper.toResource(entity.getThresholds()))
                .description(entity.getDescription())
                .endOfValidityDate(entity.getEndOfValidityDate())
                .tags(entity.getTags() == null ? null : entity.getTags().stream().map(tag -> tag.getCode()).collect(Collectors.toList()))
                .build();
    }

    @Override
    public OpenOrder toEntity(OpenOrderDto resource) {

        OpenOrder openOrder = new OpenOrder();
        openOrder.setId(resource.getId());
        openOrder.setDescription(resource.getDescription());
        openOrder.setExternalReference(resource.getExternalReference());
        openOrder.setEndOfValidityDate(resource.getEndOfValidityDate());
        return openOrder;
    }


    public void fillEntity(OpenOrder entity, OpenOrderDto resource)
    {
        entity.setDescription(resource.getDescription());
        entity.setId(resource.getId());
        entity.setDescription(resource.getDescription());
        entity.setExternalReference(resource.getExternalReference());
        entity.setEndOfValidityDate(resource.getEndOfValidityDate());
    }


}
