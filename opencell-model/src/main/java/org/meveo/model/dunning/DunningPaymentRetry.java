package org.meveo.model.dunning;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.Auditable;
import org.meveo.model.AuditableEntity;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Entity
@Table(name = "dunning_payment_retries")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_payment_retries_seq")})
public class DunningPaymentRetry extends AuditableEntity  {
	
	private static final long serialVersionUID = 1L;

	public DunningPaymentRetry() {
		super();
	}

 	public DunningPaymentRetry(Auditable auditable) {
		super(auditable);
	}

	public DunningPaymentRetry(@Size(max = 100) PaymentMethodEnum paymentMethod, @Size(max = 255) String psp,
			Integer numPayRetries, @Size(max = 255) PayRetryFrequencyUnitEnum payRetryFrequencyUnit, Integer payRetryFrequency,
			@NotNull DunningSettings dunningSettings) {
		super();
		this.paymentMethod = paymentMethod;
		this.psp = psp;
		this.numPayRetries = numPayRetries;
		this.payRetryFrequencyUnit = payRetryFrequencyUnit;
		this.payRetryFrequency = payRetryFrequency;
		this.dunningSettings = dunningSettings;
	}




	/**
	 * Payment method
	 */
	@Column(name = "payment_method", length = 100)
	@Size(max = 100)
	@Enumerated(EnumType.STRING)
	@NotNull
	private PaymentMethodEnum paymentMethod;
	
	
 	/**
	 * Payment method
	 */
	@Column(name = "psp", length = 255)
	@Size(max = 255)
	private String psp;
	
	
	/**
	 * Num payment retries
	 */
	@Column(name = "num_pay_retries")
	@NotNull
	private Integer numPayRetries;
	
	
	/**
	 * payment retry frequency unit
	 */
	@Column(name = "pay_retry_frequency_unit",length = 255)
	@Size(max = 255)
	@Enumerated(EnumType.STRING)
	@NotNull
	private PayRetryFrequencyUnitEnum payRetryFrequencyUnit;

	/**
	 * Frequency retry by days or months.
	 */
	@Column(name = "pay_retry_frequency")
	@NotNull
	private Integer payRetryFrequency;
	
	
	/**
	 * dunning settings associated to the entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dunning_settings_id",nullable = false, referencedColumnName = "id")
	@NotNull
	private DunningSettings dunningSettings;


	public PaymentMethodEnum  getPaymentMethod() {
		return paymentMethod;
	}


	public void setPaymentMethod(PaymentMethodEnum  paymentMethod) {
		this.paymentMethod = paymentMethod;
	}


	public String getPsp() {
		return psp;
	}


	public void setPsp(String psp) {
		this.psp = psp;
	}


	public Integer getNumPayRetries() {
		return numPayRetries;
	}


	public void setNumPayRetries(Integer numPayRetries) {
		this.numPayRetries = numPayRetries;
	}


	public PayRetryFrequencyUnitEnum getPayRetryFrequencyUnit() {
		return payRetryFrequencyUnit;
	}


	public void setPayRetryFrequencyUnit(PayRetryFrequencyUnitEnum payRetryFrequencyUnit) {
		this.payRetryFrequencyUnit = payRetryFrequencyUnit;
	}


	public DunningSettings getDunningSettings() {
		return dunningSettings;
	}


	public void setDunningSettings(DunningSettings dunningSettings) {
		this.dunningSettings = dunningSettings;
	}

	public Integer getPayRetryFrequency() {
		return payRetryFrequency;
	}

	public void setPayRetryFrequency(Integer payRetryFrequency) {
		this.payRetryFrequency = payRetryFrequency;
	}
}
