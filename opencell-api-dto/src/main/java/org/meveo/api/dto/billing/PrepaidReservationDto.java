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

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class PrepaidReservationDto.
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class PrepaidReservationDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1511340678838442101L;

    /** The reservation id. */
    @XmlAttribute(required = true)
    private long reservationId;

    /** The consumed quantity. */
    private BigDecimal consumedQuantity;

    /**
     * Gets the reservation id.
     *
     * @return the reservation id
     */
    public long getReservationId() {
        return reservationId;
    }

    /**
     * Sets the reservation id.
     *
     * @param reservationId the new reservation id
     */
    public void setReservationId(long reservationId) {
        this.reservationId = reservationId;
    }

    /**
     * Gets the consumed quantity.
     *
     * @return the consumed quantity
     */
    public BigDecimal getConsumedQuantity() {
        return consumedQuantity;
    }

    /**
     * Sets the consumed quantity.
     *
     * @param consumedQuantity the new consumed quantity
     */
    public void setConsumedQuantity(BigDecimal consumedQuantity) {
        this.consumedQuantity = consumedQuantity;
    }


    @Override
    public String toString() {
        return "PrepaidReservationDto [reservationId=" + reservationId + ", consumedQuantity=" + consumedQuantity + "]";
    }

}