package org.meveo.api.dto.cpq;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.ContractAccountLevel;
import org.meveo.model.cpq.enums.ProductStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@SuppressWarnings("serial")
public class ContractDto extends BusinessEntityDto {

	private ContractAccountLevel contractAccountLevel;
	private String accountCode;
	private ProductStatusEnum status;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date statusDate;
    @NotNull
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date contractDate;
    @NotNull
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date beginDate;
    @NotNull
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date endDate;
    @NotNull
	private boolean renewal;
	private int contractDuration;
    
	
	public ContractDto() {}
	
	public ContractDto(Contract c) {
		this.beginDate = c.getBeginDate();
		if(c.getBillingAccount() != null) {
			this.contractAccountLevel = ContractAccountLevel.BILLING_ACCOUNT;
			this.accountCode = c.getBillingAccount().getCode();
		}else if(c.getCustomerAccount() != null) {
			this.contractAccountLevel = ContractAccountLevel.CUSTOMER_ACCOUNT;
			this.accountCode = c.getCustomerAccount().getCode();
		}else if(c.getSeller() != null) {
			this.contractAccountLevel = ContractAccountLevel.SELLER;
			this.accountCode = c.getSeller().getCode();
		}else if(c.getCustomer() != null) {
			this.contractAccountLevel = ContractAccountLevel.CUSTOMER;
			this.accountCode = c.getCustomer().getCode();
		}
		this.code = c.getCode();
		this.contractDate = c.getContractDate();
		this.contractDuration = c.getContractDuration();
		this.endDate = c.getEndDate();
		this.renewal = c.isRenewal();
		this.status = c.getStatus();
		this.statusDate = c.getStatusDate();
		this.description = c.getDescription();
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

	/**
	 * @return the contractAccountLevel
	 */
	public ContractAccountLevel getContractAccountLevel() {
		return contractAccountLevel;
	}

	/**
	 * @param contractAccountLevel the contractAccountLevel to set
	 */
	public void setContractAccountLevel(ContractAccountLevel contractAccountLevel) {
		this.contractAccountLevel = contractAccountLevel;
	}

	/**
	 * @return the accountCode
	 */
	public String getAccountCode() {
		return accountCode;
	}

	/**
	 * @param accountCode the accountCode to set
	 */
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	
}
