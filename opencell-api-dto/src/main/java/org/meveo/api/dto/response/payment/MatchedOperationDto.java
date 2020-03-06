/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.response.payment;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
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
 * @author melyoussoufi
 * @lastModifiedVersion 7.3.0
 */
@XmlRootElement(name = "MatchedOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchedOperationDto extends BaseEntityDto {

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
    private String code;

    /** Account operation type description. */
    private String description;

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
        code = ao.getCode();
        description = ao.getDescription();
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
    public String getCode() {
        return code;
    }

    /**
     * Sets the occ code.
     *
     * @param occCode the new occ code
     */
    public void setCode(String occCode) {
        this.code = occCode;
    }

    /**
     * Gets the occ description.
     *
     * @return the occ description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the occ description.
     *
     * @param occDescription the new occ description
     */
    public void setDescription(String occDescription) {
        this.description = occDescription;
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
                + ", accountOperationId=" + accountOperationId + ", occCode=" + code + ", occDescription=" + description + ", transactionDate=" + transactionDate
                + ", transactionCategory=" + transactionCategory + ", dueDate=" + dueDate + ", transactionAmount=" + transactionAmount + ", matchedTransactionAmount="
                + matchedTransactionAmount + "]";
    }
}