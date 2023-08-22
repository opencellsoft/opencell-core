package org.meveo.api.dto.cpq;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.apiv2.cpq.contracts.BillingRuleDto;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.enums.ContractAccountLevel;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@SuppressWarnings("serial")
public class ContractDto extends EnableBusinessDto {

	@Schema(description = "contract account level, associate seller or customer or customer account or billing account to this contract",
			example = "possible value are : SELLER, CUSTOMER, CUSTOMER_ACCOUNT, BILLING_ACCOUNT")
	private ContractAccountLevel contractAccountLevel;
	@Schema(description = "account code associated to contract depending the value of  contractAccountLevel")
	private String accountCode;
	@Schema(description = "status of the contract" ,example="possible value are : DRAFT, ACTIVE, CLOSED" )
	private String status;
	@Schema(description = "date of the changement of the status, it set automatically")
	private Date statusDate;

	@Schema(description = "day of the contract")
    @NotNull
	private Date contractDate;
	@Schema(description = "begin date of the contract")
    @NotNull
	private Date beginDate;
	@Schema(description = "end date of the contract")
    @NotNull
	private Date endDate;

	@Schema(description = "renwal a the contract")
	@NotNull
	private Boolean renewal;
	@Schema(description = "duration of the contract")
	private Integer contractDuration;
	@Schema(description = "list of the custom field if any")
	private CustomFieldsDto customFields;

	@Schema(description = "list of billing rules")
	private List<BillingRuleDto> billingRules;

	@Schema(description = "An expression to decide whether the contract should be applied or not")
	private String applicationEl;
    
	
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
		this.applicationEl = c.getApplicationEl();
	}
	
	 
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
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
	
	public Boolean getRenewal() {
		return renewal;
	}

	public void setRenewal(Boolean renewal) {
		this.renewal = renewal;
	}

	
	public Integer getContractDuration() {
		return contractDuration;
	}

	public void setContractDuration(Integer contractDuration) {
		this.contractDuration = contractDuration;
	}

	public void setApplicationEl(String applicationEl) {
		this.applicationEl = applicationEl;
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

	/**
	 * @return the billing rules
	 */
	public List<BillingRuleDto> getBillingRules() {
		return billingRules;
	}

	/**
	 * @param billingRules the billing rules to set
	 */
	public void setBillingRules(List<BillingRuleDto> billingRules) {
		this.billingRules = billingRules;
	}

	/**
	 * @return the applicationEL
	 */
	public String getApplicationEl() {
		return applicationEl;
	}

	/**
	 * @param applicationEL the applicationEL to set
	 */
	public void setApplicationEL(String applicationEl) {
		this.applicationEl = applicationEl;
	}
	
}
