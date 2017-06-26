package org.meveo.api.dto.billing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class PrepaidReservationDto extends BaseDto {

	private static final long serialVersionUID = -1511340678838442101L;

	@XmlAttribute(required = true)
	public long reservationId;

	public BigDecimal consumedQuantity;

	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(long reservationId) {
		this.reservationId = reservationId;
	}

	public BigDecimal getConsumedQuantity() {
		return consumedQuantity;
	}

	public void setConsumedQuantity(BigDecimal consumedQuantity) {
		this.consumedQuantity = consumedQuantity;
	}

}
