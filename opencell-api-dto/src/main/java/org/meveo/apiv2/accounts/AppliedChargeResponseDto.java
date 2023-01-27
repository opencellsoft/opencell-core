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

package org.meveo.apiv2.accounts;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.WalletOperationDto;
import org.meveo.api.dto.response.BaseResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class AppliedChargeResponseDto.
 *
 * @author a.rouaguebe
 * 
 */
@XmlRootElement(name = "AppliedChargeResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(Include.NON_EMPTY)
public class AppliedChargeResponseDto extends BaseResponse {

    private static final long serialVersionUID = -2171674315264169687L;

	/**
     * The amount without Tax.
     */
    private BigDecimal amountWithoutTax;

    /**
     * The tax amount.
     */
    private BigDecimal amountTax;

    /**
     * The amount with tax.
     */
    private BigDecimal amountWithTax;

    /**
     * The total of wallet operations
     */
    private Integer walletOperationCount;

    /**
     * A list of EDR ids that were created
     */
    private List<Long> edrIds;

    /**
     * A list of Reservation ids
     */
    private List<Long> reservationIds;

    /**
     * A wallet operations list.
     */
    private List<WalletOperationDto> walletOperations;
    

    public AppliedChargeResponseDto() {
        setActionStatus(null);
    }

    public AppliedChargeResponseDto(CdrError processingError) {
        this.error = processingError;
        setActionStatus(null);
    }

    /**
     * CDR processing error
     */
    private CdrError error;

    /**
     * Gets the amountWithoutTax.
     * 
     * @return The amount without Tax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amountWithoutTax.
     * 
     * @param amountWithoutTax The amount without Tax
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Gets the amountTax.
     * 
     * @return the amountTax
     */
    public BigDecimal getAmountTax() {
        return amountTax;
    }

    /**
     * Sets the amountTax.
     * 
     * @param amountTax the amountTax
     */
    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * Gets the amountWithTax.
     * 
     * @return the amountWithTax.
     */
    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    /**
     * Sets the amountWithTax.
     * 
     * @param amountWithTax the amountWithTax.
     */
    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    /**
     * Gets total WO.
     * 
     * @return total WO
     */
    public Integer getWalletOperationCount() {
        return walletOperationCount;
    }

    /**
     * Sets total WO.
     * 
     * @param walletOperationCount total WO.
     */
    public void setWalletOperationCount(Integer walletOperationCount) {
        this.walletOperationCount = walletOperationCount;
    }

    /**
     * Gets walletOperations list.
     * 
     * @return walletOperations list
     */
    public List<WalletOperationDto> getWalletOperations() {
        if (walletOperations == null) {
            walletOperations = new ArrayList<>();
        }
        return walletOperations;
    }

    /**
     * Sets the walletOperations.
     * 
     * @param walletOperations wallet operations list.
     */
    public void setWalletOperations(List<WalletOperationDto> walletOperations) {
        this.walletOperations = walletOperations;
    }

    /**
     * @return CDR procesing error
     */
    public CdrError getError() {
        return error;
    }

    /**
     * @param error CDR processing error
     */
    public void setError(CdrError error) {
        this.error = error;
    }

    /**
     * @return the edrIds
     */
    public List<Long> getEdrIds() {
        return edrIds;
    }

    /**
     * @param edrIds the edrIds to set
     */
    public void setEdrIds(List<Long> edrIds) {
        this.edrIds = edrIds;
    }
    
    /**
     * @return A list of Reservation ids
     */
    public List<Long> getReservationIds() {
        return reservationIds;
    }
    
    /**
     * @param reservationIds A list of Reservation ids
     */
    public void setReservationIds(List<Long> reservationIds) {
        this.reservationIds = reservationIds;
    }

    public static class CdrError implements Serializable {

        private static final long serialVersionUID = 7006614222124909513L;

        private String errorMessage;

        public CdrError() {

        }

        public CdrError(String errorMessage) {
            super();
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

}