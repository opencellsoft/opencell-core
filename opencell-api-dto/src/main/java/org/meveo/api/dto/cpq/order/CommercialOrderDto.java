package org.meveo.api.dto.cpq.order;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.model.cpq.commercial.CommercialOrder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@SuppressWarnings("serial")
public class CommercialOrderDto extends BaseEntityDto {

	private Long id;
	@NotNull
	private String sellerCode;
	private String orderNumber;
	private String label;
	@NotNull
	private String billingAccountCode;
	private String quoteCode;
	private String contractCode;
	@NotNull
	private String orderTypeCode;
	private String invoicingPlanCode;
	//@NotNull
	private String status;
//	@NotNull
//	private Date statusDate;
	@NotNull
	private Integer orderProgress;
	@JsonSerialize(using = CustomDateSerializer.class)
	@NotNull
	private Date progressDate;
	@JsonSerialize(using = CustomDateSerializer.class)
	@NotNull
	private Date orderDate;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date realisationDate;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date customerServiceBegin;
	private int customerServiceDuration;
	private String externalReference;
	private String orderParentCode;
	private String userAccountCode;
	private AccessDto accessDto; 
	private String orderLotCode;
	
	
	public CommercialOrderDto() {
	}

	public CommercialOrderDto(CommercialOrder order) {
		this.id = order.getId();
		this.sellerCode = order.getSeller().getCode();
		this.orderNumber = order.getOrderNumber();
		this.label = order.getLabel();
		this.billingAccountCode = order.getBillingAccount().getCode();
		if(order.getQuote() != null)
			this.quoteCode = order.getQuote().getCode();
		if(order.getContract() != null)
			this.contractCode = order.getContract().getCode();
		this.orderTypeCode = order.getOrderType().getCode();
		if(order.getInvoicingPlan() != null)
			this.invoicingPlanCode = order.getInvoicingPlan().getCode();
		this.status = order.getStatus();
		this.orderProgress = order.getOrderProgress();
		this.progressDate = order.getProgressDate();
		this.orderDate = order.getOrderDate();
		this.realisationDate = order.getRealisationDate();
		this.customerServiceBegin = order.getCustomerServiceBegin();
		this.customerServiceDuration = order.getCustomerServiceDuration();
		this.externalReference = order.getExternalReference();
		if(order.getOrderParent() != null)
			this.orderParentCode = order.getOrderParent().getCode();
		if(order.getUserAccount() != null)
			this.userAccountCode = order.getUserAccount().getCode();
		if(order.getAccess() != null)
			this.accessDto = new AccessDto(order.getAccess(), null);
		if(order.getOrderLot() != null)
			this.orderLotCode = order.getOrderLot().getCode();
	}
	
	/**
	 * @return the sellerCode
	 */
	public String getSellerCode() {
		return sellerCode;
	}
	/**
	 * @param sellerCode the sellerCode to set
	 */
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	/**
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		return orderNumber;
	}
	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the billingAccountCode
	 */
	public String getBillingAccountCode() {
		return billingAccountCode;
	}
	/**
	 * @param billingAccountCode the billingAccountCode to set
	 */
	public void setBillingAccountCode(String billingAccountCode) {
		this.billingAccountCode = billingAccountCode;
	}
	/**
	 * @return the quoteCode
	 */
	public String getQuoteCode() {
		return quoteCode;
	}
	/**
	 * @param quoteCode the quoteCode to set
	 */
	public void setQuoteCode(String quoteCode) {
		this.quoteCode = quoteCode;
	}
	/**
	 * @return the contractCode
	 */
	public String getContractCode() {
		return contractCode;
	}
	/**
	 * @param contractCode the contractCode to set
	 */
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}
	/**
	 * @return the orderTypeCode
	 */
	public String getOrderTypeCode() {
		return orderTypeCode;
	}
	/**
	 * @param orderTypeCode the orderTypeCode to set
	 */
	public void setOrderTypeCode(String orderTypeCode) {
		this.orderTypeCode = orderTypeCode;
	}
	/**
	 * @return the invoicingPlanCode
	 */
	public String getInvoicingPlanCode() {
		return invoicingPlanCode;
	}
	/**
	 * @param invoicingPlanCode the invoicingPlanCode to set
	 */
	public void setInvoicingPlanCode(String invoicingPlanCode) {
		this.invoicingPlanCode = invoicingPlanCode;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the orderProgress
	 */
	public Integer getOrderProgress() {
		return orderProgress;
	}
	/**
	 * @param orderProgress the orderProgress to set
	 */
	public void setOrderProgress(Integer orderProgress) {
		this.orderProgress = orderProgress;
	}
	/**
	 * @return the progressDate
	 */
	public Date getProgressDate() {
		return progressDate;
	}
	/**
	 * @param progressDate the progressDate to set
	 */
	public void setProgressDate(Date progressDate) {
		this.progressDate = progressDate;
	}
	/**
	 * @return the orderDate
	 */
	public Date getOrderDate() {
		return orderDate;
	}
	/**
	 * @param orderDate the orderDate to set
	 */
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	/**
	 * @return the realisationDate
	 */
	public Date getRealisationDate() {
		return realisationDate;
	}
	/**
	 * @param realisationDate the realisationDate to set
	 */
	public void setRealisationDate(Date realisationDate) {
		this.realisationDate = realisationDate;
	}
	/**
	 * @return the customerServiceBegin
	 */
	public Date getCustomerServiceBegin() {
		return customerServiceBegin;
	}
	/**
	 * @param customerServiceBegin the customerServiceBegin to set
	 */
	public void setCustomerServiceBegin(Date customerServiceBegin) {
		this.customerServiceBegin = customerServiceBegin;
	}
	/**
	 * @return the customerServiceDuration
	 */
	public int getCustomerServiceDuration() {
		return customerServiceDuration;
	}
	/**
	 * @param customerServiceDuration the customerServiceDuration to set
	 */
	public void setCustomerServiceDuration(int customerServiceDuration) {
		this.customerServiceDuration = customerServiceDuration;
	}
	/**
	 * @return the externalReference
	 */
	public String getExternalReference() {
		return externalReference;
	}
	/**
	 * @param externalReference the externalReference to set
	 */
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	/**
	 * @return the orderParentCode
	 */
	public String getOrderParentCode() {
		return orderParentCode;
	}

	/**
	 * @param orderParentCode the orderParentCode to set
	 */
	public void setOrderParentCode(String orderParentCode) {
		this.orderParentCode = orderParentCode;
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

	/**
	 * @return the accessDto
	 */
	public AccessDto getAccessDto() {
		return accessDto;
	}

	/**
	 * @param accessDto the accessDto to set
	 */
	public void setAccessDto(AccessDto accessDto) {
		this.accessDto = accessDto;
	}

	/**
	 * @return the orderLotCode
	 */
	public String getOrderLotCode() {
		return orderLotCode;
	}

	/**
	 * @param orderLotCode the orderLotCode to set
	 */
	public void setOrderLotCode(String orderLotCode) {
		this.orderLotCode = orderLotCode;
	}
	
	
}
