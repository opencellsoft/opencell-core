package org.meveocrm.model.dwh;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "DWH_MEASURED_VALUE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "DWH_MEASURED_VALUE_SEQ")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasuredValue extends BaseEntity {

	private static final long serialVersionUID = -3343485468990186936L;

	@ManyToOne
	@JoinColumn(name = "MEASURABLE_QUANTITY", nullable = true, unique = false, updatable = true)
	private MeasurableQuantity measurableQuantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "MEASUREMENT_PERIOD")
	private MeasurementPeriodEnum measurementPeriod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE")
	@XmlTransient
	private Date date;

	@Column(name = "VALUE", precision = NB_PRECISION, scale = NB_DECIMALS)
	@XmlTransient
	private BigDecimal value;

	public MeasurableQuantity getMeasurableQuantity() {
		return measurableQuantity;
	}

	public void setMeasurableQuantity(MeasurableQuantity measurableQuantity) {
		this.measurableQuantity = measurableQuantity;
	}

	public MeasurementPeriodEnum getMeasurementPeriod() {
		return measurementPeriod;
	}

	public void setMeasurementPeriod(MeasurementPeriodEnum measurementPeriod) {
		this.measurementPeriod = measurementPeriod;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
