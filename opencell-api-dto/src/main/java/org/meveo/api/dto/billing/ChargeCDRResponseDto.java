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

package org.meveo.api.dto.billing;

import org.meveo.api.dto.response.BaseResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class ChargeCDRResponseDto.
 *
 * @author HORRI Khalid
 * @lastModifiedVersion 7.3
 */
@XmlRootElement(name = "ChargeCDRResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeCDRResponseDto extends BaseResponse {

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
     * A wallet operations list.
     */
    private List<WalletOperationDto> walletOperations;

    /**
     * CDR processing error
     */
    private CdrError cdrError;

    public ChargeCDRResponseDto() {
        setActionStatus(null);
    }

    public ChargeCDRResponseDto(CdrError processingError) {
        System.out.println( "this constructer ChargeCDRResponseDto" );
        this.cdrError = processingError;
        setActionStatus(null);
    }

    /**
     * Gets the cdrError.
     *
     * @return the cdrError
     */
    public CdrError getCdrError() {
        return cdrError;
    }

    /**
     * Sets the cdrError.
     *
     * @param cdrError the CdrError
     */
    public void setCdrError(CdrError cdrError) {
        this.cdrError = cdrError;
    }

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

    public static class CdrError implements Serializable {
        private static final long serialVersionUID = 5139169395026374653L;

        private String errorCode;
        private String errorMessage;
        private String cdr;

        public CdrError() {

        }

        public CdrError(String errorCode, String errorMessage, String cdr) {
            super();
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
            this.cdr = cdr;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getCdr() {
            return cdr;
        }

        public void setCdr(String cdr) {
            this.cdr = cdr;
        }
    }
}