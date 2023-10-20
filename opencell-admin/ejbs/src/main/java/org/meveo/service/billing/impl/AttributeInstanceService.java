package org.meveo.service.billing.impl;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.cpq.Attribute;
import org.meveo.service.cpq.AttributeValueService;

@Stateless
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
    
    
    public boolean checkAttributeValue(AttributeInstance attributeInstance) {
		Attribute attribute=attributeInstance.getAttribute();
		switch (attribute.getAttributeType()) {
			case TOTAL :
			case COUNT :
			case NUMERIC :
			case INTEGER:
				if(attributeInstance.getDoubleValue()==null && attributeInstance.getStringValue()==null)
					return false;
				break;
			case LIST_MULTIPLE_TEXT:
			case LIST_TEXT:
			case EXPRESSION_LANGUAGE :
			case TEXT:
				if(attributeInstance.getStringValue()==null)
					return false;
				break;

			case DATE:
				if(attributeInstance.getDateValue()==null)
					return false;
				break;
			case BOOLEAN:
				if(attributeInstance.getBooleanValue()==null)
					return false;
				break;
			default:
				break;
		}
		return true;
	}
}