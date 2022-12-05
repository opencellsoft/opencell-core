package org.meveo.service.order;

import org.meveo.model.ordering.OpenOrderTemplate;
import org.meveo.service.base.BusinessService;

import jakarta.ejb.Stateless;

@Stateless
public class OpenOrderTemplateService extends BusinessService<OpenOrderTemplate> {

    public void create(OpenOrderTemplate entity) {
        super.create(entity);
    }

    public OpenOrderTemplate update(OpenOrderTemplate entity) {
        return super.update(entity);
    }
}