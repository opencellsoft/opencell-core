package org.meveo.api.dto.response.payment;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.MatchingTypeEnum;
import org.meveo.model.payments.OperationCategoryEnum;

/**
 * Represents matched account operations
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "MatchedOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchedOperationDto extends BaseDto {

    private static final long serialVersionUID = -1796921460144396951L;

    /**
     * Matching identifier - groups several account operations matched together
     */
    private Long matchingId;

    /**
     * Matching type
     */
    private MatchingTypeEnum matchingType;

    /**
     * Date matched
     */
    private Date matchingDate;

    /**
     * Total amount matched
     */
    private BigDecimal matchingAmount;

    /**
     * Account operation identifier
     */
    private Long accountOperationId;

    /**
     * Account operation type code
     */
    private String occCode;

    /**
     * Account operation type description
     */
    private String occDescription;

    /**
     * Account operation date
     */
    private Date transactionDate;

    /**
     * Account operation type - credit, debit
     */
    private OperationCategoryEnum transactionCategory;

    /**
     * Account operation due date
     */
    private Date dueDate;

    /**
     * Account operation amount
     */
    private BigDecimal transactionAmount;

    /**
     * Currently matched account operation amount
     */
    private BigDecimal matchedTransactionAmount;

    private MatchingStatusEnum matchingStatus;

    public MatchedOperationDto() {

    }

    public MatchedOperationDto(MatchingCode matchingCode, MatchingAmount matchingAmount) {

        matchingId = matchingCode.getId();
        matchingType = matchingCode.getMatchingType();
        matchingDate = matchingCode.getMatchingDate();
        this.matchingAmount = matchingAmount.getMatchingAmount();

        AccountOperation ao = matchingAmount.getAccountOperation();

        accountOperationId = ao.getId();
        occCode = ao.getOccCode();
        occDescription = ao.getOccDescription();
        transactionDate = ao.getTransactionDate();
        transactionCategory = ao.getTransactionCategory();
        dueDate = ao.getDueDate();
        transactionAmount = ao.getAmount();
        matchedTransactionAmount = ao.getMatchingAmount();
        matchingStatus = ao.getMatchingStatus();
    }

    public Long getMatchingId() {
        return matchingId;
    }

    public void setMatchingId(Long matchingId) {
        this.matchingId = matchingId;
    }

    public MatchingTypeEnum getMatchingType() {
        return matchingType;
    }

    public void setMatchingType(MatchingTypeEnum matchingType) {
        this.matchingType = matchingType;
    }

    public Date getMatchingDate() {
        return matchingDate;
    }

    public void setMatchingDate(Date matchingDate) {
        this.matchingDate = matchingDate;
    }

    public BigDecimal getMatchingAmount() {
        return matchingAmount;
    }

    public void setMatchingAmount(BigDecimal matchingAmount) {
        this.matchingAmount = matchingAmount;
    }

    public Long getAccountOperationId() {
        return accountOperationId;
    }

    public void setAccountOperationId(Long accountOperationId) {
        this.accountOperationId = accountOperationId;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getMatchedTransactionAmount() {
        return matchedTransactionAmount;
    }

    public void setMatchedTransactionAmount(BigDecimal matchedTransactionAmount) {
        this.matchedTransactionAmount = matchedTransactionAmount;
    }

    public MatchingStatusEnum getMatchingStatus() {
        return matchingStatus;
    }

    public void setMatchingStatus(MatchingStatusEnum matchingStatus) {
        this.matchingStatus = matchingStatus;
    }

    @Override
    public String toString() {
        return "MatchedOperationDto [matchingId=" + matchingId + ", matchingType=" + matchingType + ", matchingDate=" + matchingDate + ", matchingAmount=" + matchingAmount
                + ", accountOperationId=" + accountOperationId + ", occCode=" + occCode + ", occDescription=" + occDescription + ", transactionDate=" + transactionDate
                + ", transactionCategory=" + transactionCategory + ", dueDate=" + dueDate + ", transactionAmount=" + transactionAmount + ", matchedTransactionAmount="
                + matchedTransactionAmount + "]";
    }
}