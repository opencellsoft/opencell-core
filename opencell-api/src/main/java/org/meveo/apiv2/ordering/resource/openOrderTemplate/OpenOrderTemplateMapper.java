package org.meveo.apiv2.ordering.resource.openOrderTemplate;

import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.model.ordering.OpenOrderTemplate;

public class OpenOrderTemplateMapper extends ResourceMapper<OpenOrderTemplateInput, OpenOrderTemplate> {


    @Override
    protected OpenOrderTemplateInput toResource(OpenOrderTemplate entity) {
        return null;
    }

    @Override
    public OpenOrderTemplate toEntity(OpenOrderTemplateInput resource) {

        OpenOrderTemplate openOrderTemplate = new OpenOrderTemplate();
        openOrderTemplate.setId(resource.getId());
        openOrderTemplate.setDescription(resource.getDescription());
        openOrderTemplate.setOpenOrderType(resource.getOpenOrderType());
        openOrderTemplate.setNumberOfInstantiation(resource.getNumberOfInstantiation());
        openOrderTemplate.setTemplateName(resource.getTemplateName());
        return openOrderTemplate;
    }


    public void fillEntity(OpenOrderTemplate entity, OpenOrderTemplateInput input)
    {
        entity.setDescription(input.getDescription());
        entity.setOpenOrderType(input.getOpenOrderType());
        entity.setTemplateName(input.getTemplateName());
        entity.setNumberOfInstantiation(input.getNumberOfInstantiation());
    }


}
