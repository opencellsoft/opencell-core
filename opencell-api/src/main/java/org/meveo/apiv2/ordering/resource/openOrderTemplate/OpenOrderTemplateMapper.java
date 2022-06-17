package org.meveo.apiv2.ordering.resource.openOrderTemplate;

import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.apiv2.ordering.resource.order.ImmutableOpenOrderTemplateInput;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.apiv2.ordering.resource.product.ProductMapper;
import org.meveo.model.ordering.OpenOrderTemplate;

import java.util.stream.Collectors;

public class OpenOrderTemplateMapper extends ResourceMapper<OpenOrderTemplateInput, OpenOrderTemplate> {


    private ThresholdMapper thresholdMapper = new ThresholdMapper();

    @Override
    public OpenOrderTemplateInput toResource(OpenOrderTemplate entity) {

        return ImmutableOpenOrderTemplateInput.builder()
                .id(entity.getId())
                .templateName(entity.getTemplateName())
                .openOrderType(entity.getOpenOrderType())
                .thresholds(thresholdMapper.toResource(entity.getThresholds()))
                .description(entity.getDescription())
                .products(entity.getProducts() == null ? null : entity.getProducts().stream().map(product -> product.getCode()).collect(Collectors.toList()))
                .articles(entity.getArticles() == null ? null : entity.getArticles().stream().map(accountingArticle -> accountingArticle.getCode()).collect(Collectors.toList()))
                .tags(entity.getTags() == null ? null : entity.getTags().stream().map(tag -> tag.getCode()).collect(Collectors.toList()))
                .status(entity.getStatus())
                .build();
    }

    @Override
    public OpenOrderTemplate toEntity(OpenOrderTemplateInput resource) {

        OpenOrderTemplate openOrderTemplate = new OpenOrderTemplate();
        openOrderTemplate.setId(resource.getId());
        openOrderTemplate.setDescription(resource.getDescription());
        openOrderTemplate.setOpenOrderType(resource.getOpenOrderType());
        openOrderTemplate.setTemplateName(resource.getTemplateName());
        return openOrderTemplate;
    }


    public void fillEntity(OpenOrderTemplate entity, OpenOrderTemplateInput input)
    {
        entity.setDescription(input.getDescription());
        entity.setOpenOrderType(input.getOpenOrderType());
        entity.setTemplateName(input.getTemplateName());
    }


}
