package org.meveo.api.dto.cpq.order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.cpq.OrderAttributeDto;
import org.meveo.api.dto.cpq.OrderProductDto;
import org.meveo.model.cpq.commercial.OfferLineTypeEnum;
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
	
	/** The userAccount code. */
	private String userAccountCode;
	
	/** The subscription code. */
	@Schema(description = "The subscription code") 
    private String subscriptionCode;
	
	/** Order line type */
    @Schema(description = "The order line type")
    private OfferLineTypeEnum orderLineType;
    
    /** The delivery date. */
	@Schema(description = "The termination date") 
    private Date terminationDate;
	
	/** The delivery date. */
	@Schema(description = "The termination reason") 
    private String terminationReasonCode;
	
	@Schema(description = "code of existing contract")
	private String contractCode;
	
	@Schema(description = "custom fields for order offer")
    private CustomFieldsDto customFields;

    public OrderOfferDto() {
	}
 
	
	
	public OrderOfferDto(OrderOffer orderOffer) {
		super();
		this.orderOfferId = orderOffer.getId();
		this.commercialOrderId = orderOffer.getOrder().getId();
		this.offerTemplateCode = orderOffer.getOfferTemplate().getCode();
        this.deliveryDate = orderOffer.getDeliveryDate();
        this.orderLineType = orderOffer.getOrderLineType();
        this.subscriptionCode = orderOffer.getSubscription().getCode();
        this.terminationDate = orderOffer.getTerminationDate();
        this.terminationReasonCode = orderOffer.getTerminationReason() != null ? orderOffer.getTerminationReason().getCode() : null;
        this.contractCode = orderOffer.getContract() != null ? orderOffer.getContract().getCode() : null;
	}
 

	private void init(OrderOffer orderOffer) {
		this.orderOfferId = orderOffer.getId();
		this.commercialOrderId = orderOffer.getOrder()!=null?orderOffer.getOrder().getId():null;
		this.offerTemplateCode = orderOffer.getOfferTemplate()!=null?orderOffer.getOfferTemplate().getCode():null;
		this.deliveryDate = orderOffer.getDeliveryDate();
        this.orderLineType = orderOffer.getOrderLineType();
        this.userAccountCode = orderOffer.getUserAccount()!=null?orderOffer.getUserAccount().getCode():null;
        this.subscriptionCode = orderOffer.getSubscription()!=null?orderOffer.getSubscription().getCode():null;
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



	/**
	 * @return the userAccountCode
	 */
	public String getUserAccountCode() {
		return userAccountCode;
	}



	/**
	 * @param userAccountCode the userAccountCode to set
	 */
	public void setUserAccountCode(String userAccountCode) {
		this.userAccountCode = userAccountCode;
	}

    public void setOrderLineType(OfferLineTypeEnum orderLineType) {
        this.orderLineType = orderLineType;
    }

    public OfferLineTypeEnum getOrderLineType() {
        return orderLineType;
    }

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public String getTerminationReasonCode() {
		return terminationReasonCode;
	}

	public void setTerminationReasonCode(String terminationReasonCode) {
		this.terminationReasonCode = terminationReasonCode;
	}

	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
	
}
