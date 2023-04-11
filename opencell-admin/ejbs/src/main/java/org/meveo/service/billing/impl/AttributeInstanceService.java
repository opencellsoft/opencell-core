package org.meveo.service.billing.impl;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.service.cpq.AttributeValueService;


public class AttributeInstanceService extends AttributeValueService<AttributeInstance> {

    @Override
    public void create(AttributeInstance attributeInstance) throws BusinessException {
        checkOrderAttributeMandatoryEl(attributeInstance);
        super.create(attributeInstance);
    }

    @Override
    public AttributeInstance update(AttributeInstance attributeInstance) throws BusinessException {
        checkOrderAttributeMandatoryEl(attributeInstance);
        return super.update(attributeInstance);
    }
    

    private void checkOrderAttributeMandatoryEl(AttributeInstance attributeInstance) {
    	if(!attributeInstance.getAttribute().getProductVersionAttributes().isEmpty()) {
    		if(attributeInstance.getServiceInstance() != null 
    				&& attributeInstance.getServiceInstance().getProductVersion() != null) {
	        	var productVersionAttributeOptional = findMandatoryByProductVersion(attributeInstance, attributeInstance.getServiceInstance().getProductVersion());
	        	var productVersionAttribute = productVersionAttributeOptional.orElse(null);
	        	if(productVersionAttributeOptional.isPresent()) {
	        		super.evaluateMandatoryEl(	productVersionAttribute.getValidationType(), productVersionAttribute.getValidationPattern(), productVersionAttribute.getValidationLabel(),attributeInstance,
	        				productVersionAttribute.getMandatoryWithEl(), null, null, 
	        									attributeInstance.getSubscription() != null ?  attributeInstance.getSubscription().getOrder() : null,
	        									attributeInstance.getServiceInstance());
	        	}
    		}
    		if(attributeInstance.getSubscription() != null 
    				&& attributeInstance.getSubscription().getOffer() != null) {
	    		var offerTemplateMandatoryEl = findMandatoryByOfferTemplate(attributeInstance, attributeInstance.getSubscription().getOffer());
	        	var productVersionAttribute = offerTemplateMandatoryEl.orElse(null);
			if(offerTemplateMandatoryEl.isPresent()) {
				super.evaluateMandatoryEl(	productVersionAttribute.getValidationType(), productVersionAttribute.getValidationPattern(), productVersionAttribute.getValidationLabel(), attributeInstance,
						offerTemplateMandatoryEl.get().getMandatoryWithEl(), null, null,
						attributeInstance.getSubscription() != null ?  attributeInstance.getSubscription().getOrder() : null,
						attributeInstance.getServiceInstance());
	    		}
    		}
        }
    }
}