package org.meveo.service.billing.impl;

import javax.ejb.Stateless;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.service.cpq.AttributeValueService;

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
    		if(attributeInstance.getServiceInstance() != null 
    				&& attributeInstance.getServiceInstance().getProductVersion() != null) {
	        	var mandatoryEl = findMandatoryByProductVersion(attributeInstance, attributeInstance.getServiceInstance().getProductVersion());
	        	if(mandatoryEl.isPresent() && !Strings.isEmpty(mandatoryEl.get().getMandatoryWithEl())) {
	        		super.evaluateMandatoryEl(	attributeInstance, 
	        									mandatoryEl.get().getMandatoryWithEl(), null, null, 
	        									attributeInstance.getSubscription() != null ?  attributeInstance.getSubscription().getOrder() : null,
	        									attributeInstance.getServiceInstance());
	        	}
    		}
    		if(attributeInstance.getSubscription() != null 
    				&& attributeInstance.getSubscription().getOffer() != null) {
	    		var offerTemplatMandatoryEl = findMandatoryByOfferTemplate(attributeInstance, attributeInstance.getSubscription().getOffer());
			if(offerTemplatMandatoryEl.isPresent() && !Strings.isEmpty(offerTemplatMandatoryEl.get().getMandatoryWithEl())) {
				super.evaluateMandatoryEl(	attributeInstance, 
						offerTemplatMandatoryEl.get().getMandatoryWithEl(), null, null, 
						attributeInstance.getSubscription() != null ?  attributeInstance.getSubscription().getOrder() : null,
						attributeInstance.getServiceInstance());
	    		}
    		}
        }
    }
}