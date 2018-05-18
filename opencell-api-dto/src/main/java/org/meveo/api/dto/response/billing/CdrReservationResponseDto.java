package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class CdrReservationResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CdrReservationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CdrReservationResponseDto extends BaseResponse {

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
        return "CdrReservationResponseDto [availableQuantity=" + availableQuantity + ", reservationId=" + reservationId + ", toString()=" + super.toString() + "]";
    }
}