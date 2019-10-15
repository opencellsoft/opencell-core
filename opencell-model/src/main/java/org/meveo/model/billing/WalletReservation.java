package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("R")
@NamedQueries({ @NamedQuery(name = "WalletReservation.listByReservationId", query = "SELECT r FROM WalletReservation r WHERE r.reservation.id=:reservationId") })
public class WalletReservation extends WalletOperation {

    private static final long serialVersionUID = 2757123710864061091L;

    /**
     * Prepaid consumption reservation
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    /**
     * @return Prepaid consumption reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * @param reservation Prepaid consumption reservation
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    /**
     * Constructor
     */
    public WalletReservation() {
        super();
        setStatus(WalletOperationStatusEnum.RESERVED);
    }

    /**
     * Constructor
     * 
     * @param chargeInstance Charge instance
     * @param inputQuantity Input quantity
     * @param quantityInChargeUnits Quantity in charge units
     * @param operationDate Operation date
     * @param orderNumber Order number
     * @param criteria1 Criteria 1
     * @param criteria2 Criteria 2
     * @param criteria3 Criteria 3
     * @param criteriaExtra Criteria extra
     * @param tax Tax to apply
     * @param startDate Operation date range - start date
     * @param endDate Operation date range - end date
     */
    public WalletReservation(ChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, Date operationDate, String orderNumber, String criteria1,
            String criteria2, String criteria3, String criteriaExtra, Tax tax, Date startDate, Date endDate) {

        super(chargeInstance, inputQuantity, quantityInChargeUnits, operationDate, orderNumber, criteria1, criteria2, criteria3, criteriaExtra, tax, startDate, endDate);

        setStatus(WalletOperationStatusEnum.RESERVED);
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
