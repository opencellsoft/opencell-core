package org.meveo.service.cpq.order;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.service.cpq.AttributeValueService;

import javax.ejb.Stateless;

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
            super.validateValue(orderAttribute);
        }
        super.create(orderAttribute);
    }

    @Override
    public OrderAttribute update(OrderAttribute orderAttribute) throws BusinessException {
        if (orderAttribute.getAttribute() != null
                && orderAttribute.getAttribute().getValidationPattern() != null) {
            super.validateValue(orderAttribute);
        }
        return super.update(orderAttribute);
    }
}