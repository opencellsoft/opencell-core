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
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.payments.RejectedPayment;
import org.meveo.model.payments.RejectedType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class RejectedPaymentDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RejectedPaymentDto extends AccountOperationDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4498720672406401363L;

    /** The rejected type. */
    @Schema(description = "The rejected type", example = "possible value are: A, M")
    private RejectedType rejectedType;
    
    /** The rejected date. */
    @Schema(description = "The rejected date")
    private Date rejectedDate;
    
    /** The rejected description. */
    @Schema(description = "The rejected description")
    private String rejectedDescription;
    
    /** The rejected code. */
    @Schema(description = "")
    private String rejectedCode;

    /**
     * Instantiates a new rejected payment dto.
     */
    public RejectedPaymentDto() {
        super.setType("R");
    }

    /**
     * @param rejectedPayment
     */
    public RejectedPaymentDto(RejectedPayment rejectedPayment) {
        setRejectedDate(rejectedPayment.getRejectedDate());
        setRejectedDescription(rejectedPayment.getRejectedDescription());
        setRejectedCode(rejectedPayment.getRejectedCode());
    }

    /**
     * Gets the rejected type.
     *
     * @return the rejected type
     */
    public RejectedType getRejectedType() {
        return rejectedType;
    }

    /**
     * Sets the rejected type.
     *
     * @param rejectedType the new rejected type
     */
    public void setRejectedType(RejectedType rejectedType) {
        this.rejectedType = rejectedType;
    }

    /**
     * Gets the rejected date.
     *
     * @return the rejected date
     */
    public Date getRejectedDate() {
        return rejectedDate;
    }

    /**
     * Sets the rejected date.
     *
     * @param rejectedDate the new rejected date
     */
    public void setRejectedDate(Date rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    /**
     * Gets the rejected description.
     *
     * @return the rejected description
     */
    public String getRejectedDescription() {
        return rejectedDescription;
    }

    /**
     * Sets the rejected description.
     *
     * @param rejectedDescription the new rejected description
     */
    public void setRejectedDescription(String rejectedDescription) {
        this.rejectedDescription = rejectedDescription;
    }

    /**
     * Gets the rejected code.
     *
     * @return the rejected code
     */
    public String getRejectedCode() {
        return rejectedCode;
    }

    /**
     * Sets the rejected code.
     *
     * @param rejectedCode the new rejected code
     */
    public void setRejectedCode(String rejectedCode) {
        this.rejectedCode = rejectedCode;
    }

}
