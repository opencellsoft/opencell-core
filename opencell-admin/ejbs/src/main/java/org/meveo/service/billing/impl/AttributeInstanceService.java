package org.meveo.service.billing.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.service.cpq.AttributeValueService;

import javax.ejb.Stateless;

@Stateless
public class AttributeInstanceService extends AttributeValueService<AttributeInstance> {

    @Override
    public void create(AttributeInstance attributeInstance) throws BusinessException {
        if (attributeInstance.getAttribute() != null
                && attributeInstance.getAttribute().getValidationPattern() != null) {
        	super.validateValue(attributeInstance, null, null, null, attributeInstance.getServiceInstance());
        }
        super.create(attributeInstance);
    }

    @Override
    public AttributeInstance update(AttributeInstance attributeInstance) throws BusinessException {
        if (attributeInstance.getAttribute() != null
                && attributeInstance.getAttribute().getValidationPattern() != null) {
            super.validateValue(attributeInstance, null, null, null, attributeInstance.getServiceInstance());
        }
        return super.update(attributeInstance);
    }
}