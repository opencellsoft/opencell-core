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

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class PaymentScheduleInstanceDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "PaymentScheduleInstanceBalanceDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleInstanceBalanceDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The nb schedule paid. */
    @Schema(description = "The number schedule paid")
    private int nbSchedulePaid;
    
    /** The nb schedule incoming. */
    @Schema(description = "The number schedule incoming")
    private int nbScheduleIncoming;
    
    /** The sum amount paid. */
    @Schema(description = "The sum amount paid")
    private BigDecimal sumAmountPaid;
    
    /** The sum amount incoming. */
    @Schema(description = "The sum amount incoming")
    private BigDecimal sumAmountIncoming;

    /**
     * Instantiates a new payment schedule instance balance dto.
     */
    public PaymentScheduleInstanceBalanceDto() {

    }

    /**
     * Instantiates a new payment schedule instance balance dto.
     *
     * @param nbSchedulePaid the nb schedule paid
     * @param nbScheduleIncoming the nb schedule incoming
     * @param sumAmountPaid the sum amount paid
     * @param sumAmountIncoming the sum amount incoming
     */
    public PaymentScheduleInstanceBalanceDto(int nbSchedulePaid, int nbScheduleIncoming, BigDecimal sumAmountPaid, BigDecimal sumAmountIncoming) {
        this.nbSchedulePaid = nbSchedulePaid;
        this.nbScheduleIncoming = nbScheduleIncoming;
        this.sumAmountPaid = sumAmountPaid;
        this.sumAmountIncoming = sumAmountIncoming;
    }

    /**
     * Gets the nb schedule paid.
     *
     * @return the nbSchedulePaid
     */
    public int getNbSchedulePaid() {
        return nbSchedulePaid;
    }

    /**
     * Sets the nb schedule paid.
     *
     * @param nbSchedulePaid the nbSchedulePaid to set
     */
    public void setNbSchedulePaid(int nbSchedulePaid) {
        this.nbSchedulePaid = nbSchedulePaid;
    }

    /**
     * Gets the nb schedule incoming.
     *
     * @return the nbScheduleIncoming
     */
    public int getNbScheduleIncoming() {
        return nbScheduleIncoming;
    }

    /**
     * Sets the nb schedule incoming.
     *
     * @param nbScheduleIncoming the nbScheduleIncoming to set
     */
    public void setNbScheduleIncoming(int nbScheduleIncoming) {
        this.nbScheduleIncoming = nbScheduleIncoming;
    }

    /**
     * Gets the sum amount paid.
     *
     * @return the sumAmountPaid
     */
    public BigDecimal getSumAmountPaid() {
        return sumAmountPaid;
    }

    /**
     * Sets the sum amount paid.
     *
     * @param sumAmountPaid the sumAmountPaid to set
     */
    public void setSumAmountPaid(BigDecimal sumAmountPaid) {
        this.sumAmountPaid = sumAmountPaid;
    }

    /**
     * Gets the sum amount incoming.
     *
     * @return the sumAmountIncoming
     */
    public BigDecimal getSumAmountIncoming() {
        return sumAmountIncoming;
    }

    /**
     * Sets the sum amount incoming.
     *
     * @param sumAmountIncoming the sumAmountIncoming to set
     */
    public void setSumAmountIncoming(BigDecimal sumAmountIncoming) {
        this.sumAmountIncoming = sumAmountIncoming;
    }

}
