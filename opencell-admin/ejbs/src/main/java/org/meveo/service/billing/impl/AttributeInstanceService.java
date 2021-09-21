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
        	var productVersionAttributeOptional = attributeInstance.getAttribute().getProductVersionAttributes()
        									.stream()
        									.filter(pva -> 
        										pva.getAttribute().getCode().equalsIgnoreCase(attributeInstance.getAttribute().getCode()) &&
        													pva.getProductVersion().getId() == attributeInstance.getServiceInstance().getProductVersion().getId()
        									)
        									.findFirst();
        	var productVersionAttribute = productVersionAttributeOptional.get();
        	
        	if(productVersionAttributeOptional.isPresent() && !Strings.isEmpty(productVersionAttribute.getMandatoryWithEl())) {
        		super.evaluateMandatoryEl(	productVersionAttribute.getValidationType(), 
        									productVersionAttribute.getValidationPattern(),
        									attributeInstance, 
        									productVersionAttribute.getMandatoryWithEl(), 
        									null, 
        									null, 
        									attributeInstance.getSubscription() != null ?  attributeInstance.getSubscription().getOrder() : null,
        									attributeInstance.getServiceInstance());
        	
        	}
    	}
    }
}