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
 * Represents matched account operations.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "MatchedOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchedOperationDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1796921460144396951L;

    /** Matching identifier - groups several account operations matched together. */
    private Long matchingId;

    /** Matching type. */
    private MatchingTypeEnum matchingType;

    /** Date matched. */
    private Date matchingDate;

    /** Total amount matched. */
    private BigDecimal matchingAmount;

    /** Account operation identifier. */
    private Long accountOperationId;

    /** Account operation type code. */
    private String occCode;

    /** Account operation type description. */
    private String occDescription;

    /** Account operation date. */
    private Date transactionDate;

    /** Account operation type - credit, debit. */
    private OperationCategoryEnum transactionCategory;

    /** Account operation due date. */
    private Date dueDate;

    /** Account operation amount. */
    private BigDecimal transactionAmount;

    /** Currently matched account operation amount. */
    private BigDecimal matchedTransactionAmount;

    /** The matching status. */
    private MatchingStatusEnum matchingStatus;

    /**
     * Instantiates a new matched operation dto.
     */
    public MatchedOperationDto() {

    }

    /**
     * Instantiates a new matched operation dto.
     *
     * @param matchingCode the matching code
     * @param matchingAmount the matching amount
     */
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

    /**
     * Gets the matching id.
     *
     * @return the matching id
     */
    public Long getMatchingId() {
        return matchingId;
    }

    /**
     * Sets the matching id.
     *
     * @param matchingId the new matching id
     */
    public void setMatchingId(Long matchingId) {
        this.matchingId = matchingId;
    }

    /**
     * Gets the matching type.
     *
     * @return the matching type
     */
    public MatchingTypeEnum getMatchingType() {
        return matchingType;
    }

    /**
     * Sets the matching type.
     *
     * @param matchingType the new matching type
     */
    public void setMatchingType(MatchingTypeEnum matchingType) {
        this.matchingType = matchingType;
    }

    /**
     * Gets the matching date.
     *
     * @return the matching date
     */
    public Date getMatchingDate() {
        return matchingDate;
    }

    /**
     * Sets the matching date.
     *
     * @param matchingDate the new matching date
     */
    public void setMatchingDate(Date matchingDate) {
        this.matchingDate = matchingDate;
    }

    /**
     * Gets the matching amount.
     *
     * @return the matching amount
     */
    public BigDecimal getMatchingAmount() {
        return matchingAmount;
    }

    /**
     * Sets the matching amount.
     *
     * @param matchingAmount the new matching amount
     */
    public void setMatchingAmount(BigDecimal matchingAmount) {
        this.matchingAmount = matchingAmount;
    }

    /**
     * Gets the account operation id.
     *
     * @return the account operation id
     */
    public Long getAccountOperationId() {
        return accountOperationId;
    }

    /**
     * Sets the account operation id.
     *
     * @param accountOperationId the new account operation id
     */
    public void setAccountOperationId(Long accountOperationId) {
        this.accountOperationId = accountOperationId;
    }

    /**
     * Gets the occ code.
     *
     * @return the occ code
     */
    public String getOccCode() {
        return occCode;
    }

    /**
     * Sets the occ code.
     *
     * @param occCode the new occ code
     */
    public void setOccCode(String occCode) {
        this.occCode = occCode;
    }

    /**
     * Gets the occ description.
     *
     * @return the occ description
     */
    public String getOccDescription() {
        return occDescription;
    }

    /**
     * Sets the occ description.
     *
     * @param occDescription the new occ description
     */
    public void setOccDescription(String occDescription) {
        this.occDescription = occDescription;
    }

    /**
     * Gets the transaction date.
     *
     * @return the transaction date
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transaction date.
     *
     * @param transactionDate the new transaction date
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the transaction category.
     *
     * @return the transaction category
     */
    public OperationCategoryEnum getTransactionCategory() {
        return transactionCategory;
    }

    /**
     * Sets the transaction category.
     *
     * @param transactionCategory the new transaction category
     */
    public void setTransactionCategory(OperationCategoryEnum transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    /**
     * Gets the due date.
     *
     * @return the due date
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the new due date
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the transaction amount.
     *
     * @return the transaction amount
     */
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    /**
     * Sets the transaction amount.
     *
     * @param transactionAmount the new transaction amount
     */
    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     * Gets the matched transaction amount.
     *
     * @return the matched transaction amount
     */
    public BigDecimal getMatchedTransactionAmount() {
        return matchedTransactionAmount;
    }

    /**
     * Sets the matched transaction amount.
     *
     * @param matchedTransactionAmount the new matched transaction amount
     */
    public void setMatchedTransactionAmount(BigDecimal matchedTransactionAmount) {
        this.matchedTransactionAmount = matchedTransactionAmount;
    }

    /**
     * Gets the matching status.
     *
     * @return the matching status
     */
    public MatchingStatusEnum getMatchingStatus() {
        return matchingStatus;
    }

    /**
     * Sets the matching status.
     *
     * @param matchingStatus the new matching status
     */
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