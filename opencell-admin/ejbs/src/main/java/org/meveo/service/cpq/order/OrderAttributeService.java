package org.meveo.service.cpq.order;

import javax.ejb.Stateless;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.quote.QuoteVersion;
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
        if (orderAttribute.getAttribute() != null
                && orderAttribute.getAttribute().getValidationPattern() != null) {
        	super.validateValue(orderAttribute, orderAttribute.getCommercialOrder().getQuote(), orderAttribute.getCommercialOrder().getQuoteVersion(), orderAttribute.getCommercialOrder(), null);
        	checkOrderAttributeMandatoryEl(orderAttribute);
        }
        super.create(orderAttribute);
    }

    @Override
    public OrderAttribute update(OrderAttribute orderAttribute) throws BusinessException {
        if (orderAttribute.getAttribute() != null
                && orderAttribute.getAttribute().getValidationPattern() != null) {
        	super.validateValue(orderAttribute, orderAttribute.getCommercialOrder().getQuote(), orderAttribute.getCommercialOrder().getQuoteVersion(), orderAttribute.getCommercialOrder(), null);
        	checkOrderAttributeMandatoryEl(orderAttribute);
        }
        return super.update(orderAttribute);
    }
    
    private void checkOrderAttributeMandatoryEl(OrderAttribute orderAttribute) {
    	if(!orderAttribute.getAttribute().getProductVersionAttributes().isEmpty()) {
        	var mandatoryEl = orderAttribute.getAttribute().getProductVersionAttributes()
        									.stream()
        									.filter(pva -> 
        										pva.getAttribute().getCode().equalsIgnoreCase(orderAttribute.getAttribute().getCode()) &&
        													pva.getProductVersion().getId() == orderAttribute.getOrderProduct().getProductVersion().getId()
        									)
        									.findFirst();
        	if(mandatoryEl.isPresent() && !Strings.isEmpty(mandatoryEl.get().getMandatoryWithEl())) {
        		super.evaluateMandatoryEl(orderAttribute, 
        									mandatoryEl.get().getMandatoryWithEl(), 
        									orderAttribute.getCommercialOrder() != null ? orderAttribute.getCommercialOrder().getQuote() : null, 
        									null, 
        									orderAttribute.getCommercialOrder(), 
        									null);
        	}
        }
    }
    
}