package org.meveo.api.dto.response.billing;

import java.math.BigDecimal;

public class ReservationResponseDto {
	private long ReservationId;
	private BigDecimal availableQuantity;

	public long getReservationId() {
		return ReservationId;
	}

	public void setReservationId(long reservationId) {
		ReservationId = reservationId;
	}

	public BigDecimal getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(BigDecimal availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	@Override
	public String toString() {
		return "ReservationResponseDto [ReservationId=" + ReservationId + ", availableQuantity=" + availableQuantity
				+ "]";
	}

}
