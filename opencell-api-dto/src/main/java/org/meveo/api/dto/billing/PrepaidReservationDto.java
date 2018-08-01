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
    public long reservationId;

    /** The consumed quantity. */
    public BigDecimal consumedQuantity;

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