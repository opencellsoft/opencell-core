package org.meveo.api.dto.cpq;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.commons.utils.CustomDateSerializer;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.ContractAccountLevel;
import org.meveo.model.cpq.enums.ContractStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@SuppressWarnings("serial")
public class ContractDto extends BusinessEntityDto {

	@Schema(description = "contract account level, associate seller or customer or customer account or billing account to this contract",
			example = "possible value are : SELLER, CUSTOMER, CUSTOMER_ACCOUNT, BILLING_ACCOUNT")
	private ContractAccountLevel contractAccountLevel;
	@Schema(description = "account code associated to contract depending the value of  contractAccountLevel")
	private String accountCode;
	@Schema(description = "status of the contract" ,example="possible value are : DRAFT, ACTIVE, CLOSED" )
	private ContractStatusEnum status;
	@Schema(description = "date of the changement of the status, it set automatically")
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date statusDate;

	@Schema(description = "day of the contract")
    @NotNull
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date contractDate;
	@Schema(description = "begin date of the contract")
    @NotNull
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date beginDate;
	@Schema(description = "end date of the contract")
    @NotNull
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date endDate;

	@Schema(description = "renwal a the contract")
    @NotNull
	private boolean renewal;
	@Schema(description = "duration of the contract")
	private int contractDuration;
	@Schema(description = "list of the custom field if any")
	private CustomFieldsDto customFields;

    
	
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
	
	 
	public ContractStatusEnum getStatus() {
		return status;
	}

	public void setStatus(ContractStatusEnum status) {
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

	/**
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
	
}
