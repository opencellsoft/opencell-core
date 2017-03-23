package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "CdrReservationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CdrReservationResponseDto extends BaseResponse {

	private static final long serialVersionUID = -223187140111247346L;

	private double availableQuantity;
	private long reservationId;

	public double getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(double availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(long reservationId) {
		this.reservationId = reservationId;
	}

	@Override
	public String toString() {
		return "CdrReservationResponseDto [availableQuantity=" + availableQuantity + ", reservationId=" + reservationId
				+ ", toString()=" + super.toString() + "]";
	}

}
