package org.meveo.service.cpq.order;

import jakarta.ejb.Stateless;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.service.cpq.AttributeValueService;

/**
 * @author Tarik FAKHOURI
 * @version 11.0
 * @dateCreation 19/01/2021
 */
@Stateless
public class OrderAttributeService extends AttributeValueService<OrderAttribute> {

    @Override
    public void create(OrderAttribute orderAttribute) throws BusinessException {
        checkOrderAttributeMandatoryEl(orderAttribute);
        super.create(orderAttribute);
    }

    @Override
    public OrderAttribute update(OrderAttribute orderAttribute) throws BusinessException {
        checkOrderAttributeMandatoryEl(orderAttribute);
        return super.update(orderAttribute);
    }
    
    private void checkOrderAttributeMandatoryEl(OrderAttribute orderAttribute) {
    	if(!orderAttribute.getAttribute().getProductVersionAttributes().isEmpty()) {
    		if(orderAttribute.getOrderProduct() != null 
    				&& orderAttribute.getOrderProduct().getProductVersion() != null) {
	        	var productVersionAttributeOptional = findMandatoryByProductVersion(orderAttribute, orderAttribute.getOrderProduct().getProductVersion());
	        	var productVersionAttribute = productVersionAttributeOptional.get();
	        	if(productVersionAttributeOptional.isPresent()) {
	        		super.evaluateMandatoryEl(productVersionAttribute.getValidationType(), productVersionAttribute.getValidationPattern(), productVersionAttribute.getValidationLabel(),
							orderAttribute, 
							productVersionAttribute.getMandatoryWithEl(), 
							orderAttribute.getCommercialOrder() != null ? orderAttribute.getCommercialOrder().getQuote() : null, 
							null, 
							orderAttribute.getCommercialOrder(), 
							null);
	        	}
    		}
    		if(orderAttribute.getOrderOffer() != null 
    				&&  orderAttribute.getOrderOffer().getOfferTemplate() != null ) {
	    		var offerTemplatMandatoryEl = findMandatoryByOfferTemplate(orderAttribute, orderAttribute.getOrderOffer().getOfferTemplate());
				if(offerTemplatMandatoryEl.isPresent()) {
	    			var offerTempalteAttribute = offerTemplatMandatoryEl.get();
					super.evaluateMandatoryEl(offerTempalteAttribute.getValidationType(), offerTempalteAttribute.getValidationPattern(), offerTempalteAttribute.getValidationLabel(),
							orderAttribute, 
							offerTemplatMandatoryEl.get().getMandatoryWithEl(), 
							orderAttribute.getCommercialOrder() != null ? orderAttribute.getCommercialOrder().getQuote() : null, 
							null, 
							orderAttribute.getCommercialOrder(), 
							null);
		    		}
    		}
        }
    }
    
}