package org.meveo.api.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.order.OrderOfferDto;
import org.meveo.api.dto.cpq.order.OrderTypeDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderType;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.meveo.service.cpq.order.OrderOfferService;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;


/**
 * @author -Mbarek-Ay
 * @version 11.0
 *
 */
@Stateless
public class OrderOfferApi extends BaseApi {

	@Inject 
	private OrderOfferService orderOfferService; 
	
	@Inject 
	private OfferTemplateService offerTemplateService; 
	
	@Inject 
	private CommercialOrderService commercialOrderService;
	

    
    
	public OrderOfferDto create(OrderOfferDto orderOfferDto) throws MeveoApiException, BusinessException {

		if (orderOfferDto.getCommercialOrderId()==null) {
			missingParameters.add("commercialOrderId");
		}
		if (StringUtils.isBlank(orderOfferDto.getOfferTemplateCode())) {
			missingParameters.add("offerTemplateCode");
		}
		handleMissingParametersAndValidate(orderOfferDto);

		CommercialOrder commercialOrder = commercialOrderService.findById(orderOfferDto.getCommercialOrderId());

		if ( commercialOrder!= null) {
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderOfferDto.getCommercialOrderId());
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(orderOfferDto.getOfferTemplateCode());
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, orderOfferDto.getOfferTemplateCode());
		}
		OrderOffer orderOffer = new OrderOffer();
		orderOffer.setOrder(commercialOrder);
		orderOffer.setOfferTemplate(offerTemplate);
		orderOfferService.create(orderOffer);
		orderOfferDto.setOrderOfferId(orderOffer.getId());
		return new OrderOfferDto(orderOffer);
	} 
	public OrderOfferDto update(OrderOfferDto orderOfferDto) throws MeveoApiException, BusinessException {

		if (orderOfferDto.getId()==null) {
			missingParameters.add("id");
		}
		OrderOffer orderOffer = orderOfferService.findById(orderOfferDto.getId());
		if (orderOffer == null) {
			throw new EntityDoesNotExistsException(ProductOrder.class, orderOfferDto.getId());
		}
		handleMissingParametersAndValidate(orderOfferDto);

		CommercialOrder commercialOrder = commercialOrderService.findById(orderOfferDto.getCommercialOrderId());

		if ( commercialOrder!= null) {
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderOfferDto.getCommercialOrderId());
		}

		OfferTemplate offerTemplate = offerTemplateService.findByCode(orderOfferDto.getOfferTemplateCode());
		if (offerTemplate == null) {
			throw new EntityDoesNotExistsException(OfferTemplate.class, orderOfferDto.getOfferTemplateCode());
		} 
		orderOffer.setOrder(commercialOrder);
		orderOffer.setOfferTemplate(offerTemplate);
		orderOfferService.update(orderOffer);
		return new OrderOfferDto(orderOffer) ;
	}
     
    public void remove(Long id) throws MeveoApiException, BusinessException { 
    	OrderOffer orderOffer = orderOfferService.findById(id);
    	if (orderOffer == null) {
    		throw new EntityDoesNotExistsException(ProductOrder.class, id);
    	} 
    	orderOfferService.remove(orderOffer);

    }
    
	public OrderOfferDto find(Long id) {
		if(id==null)
			missingParameters.add("id");
	    OrderOffer orderOffer = orderOfferService.findById(id);
		if(orderOffer == null)
			throw new EntityDoesNotExistsException(OrderOffer.class, id);
		return new OrderOfferDto(orderOffer);
	}
}
