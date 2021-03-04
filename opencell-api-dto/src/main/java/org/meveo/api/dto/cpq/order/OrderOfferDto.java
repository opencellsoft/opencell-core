package org.meveo.api.dto.cpq.order;

import java.util.ArrayList;
import java.util.List;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.cpq.commercial.OrderOffer;

@SuppressWarnings("serial") 
public class OrderOfferDto extends BusinessEntityDto {
 
	private Long orderOfferId;
	
    /** The commercial order id. */ 
    private Long commercialOrderId;
	
	
    /** The offer template code. */ 
    private String offerTemplateCode;
    
    private List<OrderProductDTO> products = new ArrayList<OrderProductDTO>();
     
	
	public OrderOfferDto() {
	}
 
	
	
	public OrderOfferDto(OrderOffer orderOffer) {
		super();
		this.orderOfferId = orderOffer.getId();
		this.commercialOrderId = orderOffer.getOrder().getId();
		this.offerTemplateCode = orderOffer.getOfferTemplate().getCode(); 
	}
 

	/**
	 * @return the commercialOrderId
	 */
	public Long getCommercialOrderId() {
		return commercialOrderId;
	}

 
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}




	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
 




	
	
	
}
