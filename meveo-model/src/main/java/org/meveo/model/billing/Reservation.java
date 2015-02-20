package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;
import org.meveo.model.rating.EDR;

@Entity
@Table(name = "BILLING_RESERVATION")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_RESERVATION_SEQ")
public class Reservation extends AuditableEntity {

	private static final long serialVersionUID = 4110616902439820101L;

	@Column(name = "INPUT_MESSAGE")
	private String inputMessage;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "RESERVATION_DATE")
	private Date reservationDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "EXPIRY_DATE")
	private Date expiryDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private ReservationStatus status;

	@ManyToOne
	@JoinColumn(name = "USER_ACCOUNT_ID")
	private UserAccount userAccount;

	@OneToOne
	@JoinColumn(name = "SUBSCRIPTION_ID")
	private Subscription subscription;

	@ManyToOne
	@JoinColumn(name = "WALLET_ID")
	private WalletInstance wallet;
	
	@Column(name = "QUANTITY", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal quantity;
	
	@Column(name = "AMOUNT_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithoutTax = BigDecimal.ZERO;

	@Column(name = "AMOUNT_WITH_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithTax = BigDecimal.ZERO;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "BILLING_RESRV_COUNTID")
	private Map<Long,BigDecimal> counterPeriodValues = new HashMap<Long, BigDecimal>(); 

    @Column(name="ORIGIN_EDR")
    private EDR originEdr;
    
	public String getInputMessage() {
		return inputMessage;
	}

	public void setInputMessage(String inputMessage) {
		this.inputMessage = inputMessage;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public ReservationStatus getStatus() {
		return status;
	}

	public void setStatus(ReservationStatus status) {
		this.status = status;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public WalletInstance getWallet() {
		return wallet;
	}

	public void setWallet(WalletInstance wallet) {
		this.wallet = wallet;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public Date getReservationDate() {
		return reservationDate;
	}

	public void setReservationDate(Date reservationDate) {
		this.reservationDate = reservationDate;
	}

	public Map<Long, BigDecimal> getCounterPeriodValues() {
		return counterPeriodValues;
	}

	public void setCounterPeriodValues(Map<Long, BigDecimal> counterPeriodValues) {
		this.counterPeriodValues = counterPeriodValues;
	}

	public EDR getOriginEdr() {
		return originEdr;
	}

	public void setOriginEdr(EDR originEdr) {
		this.originEdr = originEdr;
	}


}
