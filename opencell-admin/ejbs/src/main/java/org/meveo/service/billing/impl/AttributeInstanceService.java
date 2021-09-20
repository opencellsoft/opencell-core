package org.meveo.service.billing.impl;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.service.cpq.AttributeValueService;

import javax.ejb.Stateless;

@Stateless
public class AttributeInstanceService extends AttributeValueService<AttributeInstance> {

    @Override
    public void create(AttributeInstance attributeInstance) throws BusinessException {
        if (attributeInstance.getAttribute() != null
                && attributeInstance.getAttribute().getValidationPattern() != null) {
        	super.validateValue(attributeInstance, null, null, null, attributeInstance.getServiceInstance());
            checkOrderAttributeMandatoryEl(attributeInstance);
        }
        super.create(attributeInstance);
    }

    @Override
    public AttributeInstance update(AttributeInstance attributeInstance) throws BusinessException {
        if (attributeInstance.getAttribute() != null
                && attributeInstance.getAttribute().getValidationPattern() != null) {
            super.validateValue(attributeInstance, null, null, null, attributeInstance.getServiceInstance());
            checkOrderAttributeMandatoryEl(attributeInstance);
        }
        return super.update(attributeInstance);
    }
    

    private void checkOrderAttributeMandatoryEl(AttributeInstance attributeInstance) {
    	if(!attributeInstance.getAttribute().getProductVersionAttributes().isEmpty()) {
        	var mandatoryEl = attributeInstance.getAttribute().getProductVersionAttributes()
        									.stream()
        									.filter(pva -> 
        										pva.getAttribute().getCode().equalsIgnoreCase(attributeInstance.getAttribute().getCode()) &&
        													pva.getProductVersion().getId() == attributeInstance.getServiceInstance().getProductVersion().getId()
        									)
        									.findFirst();
        	if(mandatoryEl.isPresent() && !Strings.isEmpty(mandatoryEl.get().getMandatoryWithEl())) {
        		super.evaluateMandatoryEl(	attributeInstance, 
        									mandatoryEl.get().getMandatoryWithEl(), 
        									null, 
        									null, 
        									attributeInstance.getSubscription() != null ?  attributeInstance.getSubscription().getOrder() : null,
        									attributeInstance.getServiceInstance());
        	}
        }
    }
}