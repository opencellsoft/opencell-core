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

package org.meveo.api.dto.payment;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.model.payments.MatchingTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class MatchingCodeDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "MatchingCode")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingCodeDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5657981714421497476L;

    /** The code. */
    @Schema(description = "The code of matching code")
    private String code;
    
    /** The matching type. */
    @Schema(description = "The matching type", example = "possible value are: A, M, A_TIP, A_DERICT_DEBIT")
    private MatchingTypeEnum matchingType;
    
    /** The matching date. */
    @Schema(description = "The matching date")
    private Date matchingDate;
    
    /** The matching amount credit. */
    @Schema(description = "The matching amount credit")
    private BigDecimal matchingAmountCredit;
    
    /** The matching amount debit. */
    @Schema(description = "The matching amount debit")
    private BigDecimal matchingAmountDebit;

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
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
     * Gets the matching amount credit.
     *
     * @return the matching amount credit
     */
    public BigDecimal getMatchingAmountCredit() {
        return matchingAmountCredit;
    }

    /**
     * Sets the matching amount credit.
     *
     * @param matchingAmountCredit the new matching amount credit
     */
    public void setMatchingAmountCredit(BigDecimal matchingAmountCredit) {
        this.matchingAmountCredit = matchingAmountCredit;
    }

    /**
     * Gets the matching amount debit.
     *
     * @return the matching amount debit
     */
    public BigDecimal getMatchingAmountDebit() {
        return matchingAmountDebit;
    }

    /**
     * Sets the matching amount debit.
     *
     * @param matchingAmountDebit the new matching amount debit
     */
    public void setMatchingAmountDebit(BigDecimal matchingAmountDebit) {
        this.matchingAmountDebit = matchingAmountDebit;
    }

}