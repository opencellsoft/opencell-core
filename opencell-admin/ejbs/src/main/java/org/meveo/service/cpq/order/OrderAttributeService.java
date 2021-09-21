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
        	var productVersionAttributeOptional = orderAttribute.getAttribute().getProductVersionAttributes()
        									.stream()
        									.filter(pva -> 
        										pva.getAttribute().getCode().equalsIgnoreCase(orderAttribute.getAttribute().getCode()) &&
        													pva.getProductVersion().getId() == orderAttribute.getOrderProduct().getProductVersion().getId()
        									)
        									.findFirst();
        	var productVersionAttribute = productVersionAttributeOptional.get();
        	if(productVersionAttributeOptional.isPresent() && !Strings.isEmpty(productVersionAttribute.getMandatoryWithEl())) {
        		super.evaluateMandatoryEl(productVersionAttribute.getValidationType(), productVersionAttribute.getValidationPattern(),
        									orderAttribute, 
        									productVersionAttribute.getMandatoryWithEl(), 
        									orderAttribute.getCommercialOrder() != null ? orderAttribute.getCommercialOrder().getQuote() : null, 
        									null, 
        									orderAttribute.getCommercialOrder(), 
        									null);
        	}
        }
    }
    
}