package org.meveo.api.dto.payment;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;

@XmlRootElement(name = "AccountOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountOperationDto extends BaseDto {

	private static final long serialVersionUID = 4329241417200680028L;

	private Long id;
	private Date dueDate;
	private String type;
	private Date transactionDate;
	private OperationCategoryEnum transactionCategory;
	private String reference;
	private String accountCode;
	private String accountCodeClientSide;
	private BigDecimal amount;
	private BigDecimal matchingAmount = BigDecimal.ZERO;
	private BigDecimal unMatchingAmount = BigDecimal.ZERO;
	private MatchingStatusEnum matchingStatus;
	private String occCode;
	private String occDescription;
	private String customerAccount;
	private Boolean excludedFromDunning;

	private MatchingAmountsDto matchingAmounts;
	private OtherCreditAndChargeDto otherCreditAndCharge;
	private RecordedInvoiceDto recordedInvoice;
	private RejectedPaymentDto rejectedPayment;

	private String bankLot;
	private String bankReference;
	private Date bankCollectionDate;
	private Date depositDate;
	
	private CustomFieldsDto customFields;

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

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public OperationCategoryEnum getTransactionCategory() {
		return transactionCategory;
	}

	public void setTransactionCategory(OperationCategoryEnum transactionCategory) {
		this.transactionCategory = transactionCategory;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getAccountCodeClientSide() {
		return accountCodeClientSide;
	}

	public void setAccountCodeClientSide(String accountCodeClientSide) {
		this.accountCodeClientSide = accountCodeClientSide;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getMatchingAmount() {
		return matchingAmount;
	}

	public void setMatchingAmount(BigDecimal matchingAmount) {
		this.matchingAmount = matchingAmount;
	}

	public BigDecimal getUnMatchingAmount() {
		return unMatchingAmount;
	}

	public void setUnMatchingAmount(BigDecimal unMatchingAmount) {
		this.unMatchingAmount = unMatchingAmount;
	}

	public MatchingStatusEnum getMatchingStatus() {
		return matchingStatus;
	}

	public void setMatchingStatus(MatchingStatusEnum matchingStatus) {
		this.matchingStatus = matchingStatus;
	}

	public String getOccCode() {
		return occCode;
	}

	public void setOccCode(String occCode) {
		this.occCode = occCode;
	}

	public String getOccDescription() {
		return occDescription;
	}

	public void setOccDescription(String occDescription) {
		this.occDescription = occDescription;
	}

	public String getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(String customerAccount) {
		this.customerAccount = customerAccount;
	}

	public Boolean getExcludedFromDunning() {
		return excludedFromDunning;
	}

	public void setExcludedFromDunning(Boolean excludedFromDunning) {
		this.excludedFromDunning = excludedFromDunning;
	}

	public MatchingAmountsDto getMatchingAmounts() {
		return matchingAmounts;
	}

	public void setMatchingAmounts(MatchingAmountsDto matchingAmounts) {
		this.matchingAmounts = matchingAmounts;
	}

	public OtherCreditAndChargeDto getOtherCreditAndCharge() {
		return otherCreditAndCharge;
	}

	public void setOtherCreditAndCharge(OtherCreditAndChargeDto otherCreditAndCharge) {
		this.otherCreditAndCharge = otherCreditAndCharge;
	}

	public RecordedInvoiceDto getRecordedInvoice() {
		return recordedInvoice;
	}

	public void setRecordedInvoice(RecordedInvoiceDto recordedInvoice) {
		this.recordedInvoice = recordedInvoice;
	}

	public RejectedPaymentDto getRejectedPayment() {
		return rejectedPayment;
	}

	public void setRejectedPayment(RejectedPaymentDto rejectedPayment) {
		this.rejectedPayment = rejectedPayment;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getBankLot() {
		return bankLot;
	}

	public void setBankLot(String bankLot) {
		this.bankLot = bankLot;
	}

	public String getBankReference() {
		return bankReference;
	}

	public void setBankReference(String bankReference) {
		this.bankReference = bankReference;
	}

	public Date getBankCollectionDate() {
		return bankCollectionDate;
	}

	public void setBankCollectionDate(Date bankCollectionDate) {
		this.bankCollectionDate = bankCollectionDate;
	}

	public Date getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}
}
