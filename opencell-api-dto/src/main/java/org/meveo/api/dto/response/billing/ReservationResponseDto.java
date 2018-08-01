package org.meveo.api.dto.response.billing;

import java.math.BigDecimal;

/**
 * The Class ReservationResponseDto.
 * 
 * @author anasseh
 */
public class ReservationResponseDto {
    
    /** The Reservation id. */
    private long ReservationId;
    
    /** The available quantity. */
    private BigDecimal availableQuantity;

    /**
     * Gets the reservation id.
     *
     * @return the reservation id
     */
    public long getReservationId() {
        return ReservationId;
    }

    /**
     * Sets the reservation id.
     *
     * @param reservationId the new reservation id
     */
    public void setReservationId(long reservationId) {
        ReservationId = reservationId;
    }

    /**
     * Gets the available quantity.
     *
     * @return the available quantity
     */
    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Sets the available quantity.
     *
     * @param availableQuantity the new available quantity
     */
    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String toString() {
        return "ReservationResponseDto [ReservationId=" + ReservationId + ", availableQuantity=" + availableQuantity + "]";
    }
}