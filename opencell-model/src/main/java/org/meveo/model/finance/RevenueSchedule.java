package org.meveo.model.finance;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.ChargeInstance;

@Entity
@Table(name = "AR_REVENUE_SCHEDULE", uniqueConstraints = @UniqueConstraint(columnNames = { "CHARGE_INSTANCE_ID", "REVENUE_DATE" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "AR_REVENUE_SCHEDULE_SEQ"), })
public class RevenueSchedule extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7793758853731725829L;

	@ManyToOne(optional=false)
	@JoinColumn(name = "CHARGE_INSTANCE_ID")
	@NotNull
	private ChargeInstance chargeInstance;
	
	@Column(name="REVENUE_DATE",nullable=false)
	@NotNull
	private Date revenueDate;
	
	@Column(name="RECOGNIZED_REVENUE")
	private BigDecimal recognizedRevenue=BigDecimal.ZERO;

	@Column(name="INVOICED_REVENUE")
	private BigDecimal invoicedRevenue=BigDecimal.ZERO;

	@Column(name="ACCRUED_REVENUE")
	private BigDecimal accruedRevenue=BigDecimal.ZERO;

	@Column(name="DEFFERED_REVENUE")
	private BigDecimal defferedRevenue=BigDecimal.ZERO;

	public ChargeInstance getChargeInstance() {
		return chargeInstance;
	}

	public void setChargeInstance(ChargeInstance chargeInstance) {
		this.chargeInstance = chargeInstance;
	}

	public Date getRevenueDate() {
		return revenueDate;
	}

	public void setRevenueDate(Date revenueDate) {
		this.revenueDate = revenueDate;
	}

	public BigDecimal getRecognizedRevenue() {
		return recognizedRevenue;
	}

	public void setRecognizedRevenue(BigDecimal recognizedRevenue) {
		this.recognizedRevenue = recognizedRevenue;
	}

	public BigDecimal getInvoicedRevenue() {
		return invoicedRevenue;
	}

	public void setInvoicedRevenue(BigDecimal invoicedRevenue) {
		this.invoicedRevenue = invoicedRevenue;
	}

	public BigDecimal getAccruedRevenue() {
		return accruedRevenue;
	}

	public void setAccruedRevenue(BigDecimal accruedRevenue) {
		this.accruedRevenue = accruedRevenue;
	}

	public BigDecimal getDefferedRevenue() {
		return defferedRevenue;
	}

	public void setDefferedRevenue(BigDecimal defferedRevenue) {
		this.defferedRevenue = defferedRevenue;
	}
	
	
}
