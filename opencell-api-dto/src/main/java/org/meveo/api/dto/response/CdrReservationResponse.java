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

package org.meveo.api.dto.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class CdrReservationResponse.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CdrReservationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CdrReservationResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -223187140111247346L;

    /** The available quantity. */
    private double availableQuantity;
    
    /** The reservation id. */
    private long reservationId;

    /**
     * Gets the available quantity.
     *
     * @return the available quantity
     */
    public double getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Sets the available quantity.
     *
     * @param availableQuantity the new available quantity
     */
    public void setAvailableQuantity(double availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

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


    @Override
    public String toString() {
        return "CdrReservationResponse [availableQuantity=" + availableQuantity + ", reservationId=" + reservationId + ", toString()=" + super.toString() + "]";
    }

}
