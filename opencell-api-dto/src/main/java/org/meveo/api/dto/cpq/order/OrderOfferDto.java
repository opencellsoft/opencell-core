package org.meveo.api.dto.cpq.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.cpq.OrderAttributeDto;
import org.meveo.api.dto.cpq.OrderProductDto;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;

import io.swagger.v3.oas.annotations.media.Schema;

@SuppressWarnings("serial") 
public class OrderOfferDto extends BaseEntityDto {
 
	@Schema(description = "The id of the order offer")
	private Long orderOfferId;
	
    /** The commercial order id. */
	@Schema(description = "The commercial order id") 
    private Long commercialOrderId;
	
	
    /** The offer template code. */
	@Schema(description = "The offer template code") 
    private String offerTemplateCode;
    
    /** The discountPlan code. */
	@Schema(description = "The discountPlan code") 
    private String discountPlanCode;
    
    private List<OrderProductDto> orderProducts = new ArrayList<OrderProductDto>();
     
    private List<OrderAttributeDto> orderAttributes =new ArrayList<OrderAttributeDto>();
    
    /** The delivery date. */
	@Schema(description = "The delivery date") 
    private Date deliveryDate;
	
	public OrderOfferDto() {
	}
 
	
	
	public OrderOfferDto(OrderOffer orderOffer) {
		super();
		this.orderOfferId = orderOffer.getId();
		this.commercialOrderId = orderOffer.getOrder().getId();
		this.offerTemplateCode = orderOffer.getOfferTemplate().getCode();
		this.deliveryDate = orderOffer.getDeliveryDate();
	}
 

	private void init(OrderOffer orderOffer) {
		this.orderOfferId = orderOffer.getId();
		this.commercialOrderId = orderOffer.getOrder()!=null?orderOffer.getOrder().getId():null;
		this.offerTemplateCode = orderOffer.getOfferTemplate()!=null?orderOffer.getOfferTemplate().getCode():null;
	}
	public OrderOfferDto(OrderOffer orderOffer, boolean loadOrderProduct, boolean loadOrderProdAttribute,boolean loadOrderAttributes) {
		init(orderOffer);
		if(loadOrderProduct) {
			orderProducts=new ArrayList<OrderProductDto>();
			for(OrderProduct orderProduct:orderOffer.getProducts()) {
				orderProducts.add(new OrderProductDto(orderProduct,loadOrderProdAttribute));
			}
		}
		if(loadOrderAttributes) {
			orderAttributes=new ArrayList<OrderAttributeDto>();
			for(OrderAttribute orderAttribute:orderOffer.getOrderAttributes()) {
				orderAttributes.add(new OrderAttributeDto(orderAttribute));
			}
	}
	}
	
	
	/**
	 * @return the commercialOrderId
	 */
	public Long getCommercialOrderId() {
		return commercialOrderId;
	}
  
	/**
	 * @param commercialOrderId the commercialOrderId to set
	 */
	public void setCommercialOrderId(Long commercialOrderId) {
		this.commercialOrderId = commercialOrderId;
	}

	/**
	 * @return the offerTemplateCode
	 */
	public String getOfferTemplateCode() {
		return offerTemplateCode;
	}

	/**
	 * @param offerTemplateCode the offerTemplateCode to set
	 */
	public void setOfferTemplateCode(String offerTemplateCode) {
		this.offerTemplateCode = offerTemplateCode;
	}



	/**
	 * @return the orderOfferId
	 */
	public Long getOrderOfferId() {
		return orderOfferId;
	}



	/**
	 * @param orderOfferId the orderOfferId to set
	 */
	public void setOrderOfferId(Long orderOfferId) {
		this.orderOfferId = orderOfferId;
	}





	/**
	 * @return the orderProducts
	 */
	public List<OrderProductDto> getOrderProducts() {
		return orderProducts;
	}



	/**
	 * @param orderProducts the orderProducts to set
	 */
	public void setOrderProducts(List<OrderProductDto> orderProducts) {
		this.orderProducts = orderProducts;
	}



	/**
	 * @return the orderAttributes
	 */
	public List<OrderAttributeDto> getOrderAttributes() {
		return orderAttributes;
	}



	/**
	 * @param orderAttributes the orderAttributes to set
	 */
	public void setOrderAttributes(List<OrderAttributeDto> orderAttributes) {
		this.orderAttributes = orderAttributes;
	}



	public String getDiscountPlanCode() {
		return discountPlanCode;
	}



	public void setDiscountPlanCode(String discountPlanCode) {
		this.discountPlanCode = discountPlanCode;
	}



	public Date getDeliveryDate() {
		return deliveryDate;
	}



	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
 
}
