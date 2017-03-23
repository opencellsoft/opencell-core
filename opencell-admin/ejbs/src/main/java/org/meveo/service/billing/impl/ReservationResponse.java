package org.meveo.service.billing.impl;

import java.math.BigDecimal;

public class ReservationResponse {
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
	
}
