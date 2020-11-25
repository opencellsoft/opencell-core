package org.meveo.api.dto.cpq;

import java.util.Date;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.ProductStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@SuppressWarnings("serial")
public class ContractDto extends BusinessEntityDto {

	private String sellerCode;
	private String billingAccountCode;
	private String customerAccountCode;
	private String customerCode;
	private ProductStatusEnum status;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date statusDate;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date contractDate;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date beginDate;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date endDate;
	private boolean renewal;
	private int contractDuration;
    
	
	public ContractDto() {}
	
	public ContractDto(Contract c) {
		this.beginDate = c.getBeginDate();
		if(c.getBillingAccount() != null)
			this.billingAccountCode = c.getBillingAccount().getCode();
		this.code = c.getCode();
		this.contractDate = c.getContractDate();
		this.contractDuration = c.getContractDuration();
		if(c.getCustomerAccount() != null)
			this.customerAccountCode = c.getCustomerAccount().getCode();
		this.endDate = c.getEndDate();
		this.renewal = c.isRenewal();
		if(c.getSeller() != null)
			this.sellerCode = c.getSeller().getCode();
		this.status = c.getStatus();
		this.statusDate = c.getStatusDate();
		if(c.getCustomer() != null)
			this.customerCode = c.getCustomer().getCode();
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
	 * @return the customerAccountCode
	 */
	public String getCustomerAccountCode() {
		return customerAccountCode;
	}
	/**
	 * @param customerAccountCode the customerAccountCode to set
	 */
	public void setCustomerAccountCode(String customerAccountCode) {
		this.customerAccountCode = customerAccountCode;
	}
	/**
	 * @return the customerCode
	 */
	public String getCustomerCode() {
		return customerCode;
	}
	/**
	 * @param customerCode the customerCode to set
	 */
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	/**
	 * @return the status
	 */
	public ProductStatusEnum getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(ProductStatusEnum status) {
		this.status = status;
	}
	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}
	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}
	/**
	 * @return the contractDate
	 */
	public Date getContractDate() {
		return contractDate;
	}
	/**
	 * @param contractDate the contractDate to set
	 */
	public void setContractDate(Date contractDate) {
		this.contractDate = contractDate;
	}
	/**
	 * @return the beginDate
	 */
	public Date getBeginDate() {
		return beginDate;
	}
	/**
	 * @param beginDate the beginDate to set
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the renewal
	 */
	public boolean isRenewal() {
		return renewal;
	}
	/**
	 * @param renewal the renewal to set
	 */
	public void setRenewal(boolean renewal) {
		this.renewal = renewal;
	}
	/**
	 * @return the contractDuration
	 */
	public int getContractDuration() {
		return contractDuration;
	}
	/**
	 * @param contractDuration the contractDuration to set
	 */
	public void setContractDuration(int contractDuration) {
		this.contractDuration = contractDuration;
	}
	
}
