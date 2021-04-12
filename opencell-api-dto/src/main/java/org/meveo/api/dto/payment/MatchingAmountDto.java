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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class MatchingAmountDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "MatchingAmount")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingAmountDto extends AuditableEntityDto {

    /**
     * The auto generated serial no
     */
    private static final long serialVersionUID = 8509128338503816584L;

    /** The matching code. */
    @Schema(description = "The matching code")
    private String matchingCode;
    
    /** The matching amount. */
    @Schema(description = "The matching amount")
    private BigDecimal matchingAmount;
    
    /** The matching codes. */
    @Schema(description = "The matching codes")
    private MatchingCodesDto matchingCodes;

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
     * Gets the matching codes.
     *
     * @return the matching codes
     */
    public MatchingCodesDto getMatchingCodes() {
        return matchingCodes;
    }

    /**
     * Sets the matching codes.
     *
     * @param matchingCodes the new matching codes
     */
    public void setMatchingCodes(MatchingCodesDto matchingCodes) {
        this.matchingCodes = matchingCodes;
    }

    /**
     * Gets the matching code.
     *
     * @return the matching code
     */
    public String getMatchingCode() {
        return matchingCode;
    }

    /**
     * Sets the matching code.
     *
     * @param matchingCode the new matching code
     */
    public void setMatchingCode(String matchingCode) {
        this.matchingCode = matchingCode;
    }

}