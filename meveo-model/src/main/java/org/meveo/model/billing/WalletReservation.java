package org.meveo.model.billing;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("R")
@NamedQueries({
	@NamedQuery(name = "WalletReservation.listByReservationId", 
			query = "SELECT r FROM WalletReservation r WHERE r.reservation.id=:reservationId")
})
public class WalletReservation extends WalletOperation {

	private static final long serialVersionUID = 2757123710864061091L;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "RESERVATION_ID")
	private Reservation reservation;

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
	
	@Override
	@Transient
	public WalletOperation getUnratedClone() {
		WalletReservation result = new WalletReservation();
		fillUnratedClone(result);
		result.setReservation(reservation);
		return result;
	}
}
