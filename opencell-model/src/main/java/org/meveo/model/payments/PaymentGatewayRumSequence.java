package org.meveo.model.payments;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.sequence.GenericSequence;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
@Entity
@ExportIdentifier({ "code", "seller.code" })
@Table(name = "ar_payment_gateway_rum_sequence")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "ar_payment_gateway_rum_sequence_seq"), })
public class PaymentGatewayRumSequence extends BusinessEntity {

	/**
	 * Serial.
	 */
	private static final long serialVersionUID = -8224845537101585519L;

	/**
	 * Sequence rule.
	 */
	@Embedded
	private GenericSequence genericSequence = new GenericSequence();

	/**
	 * PaymentGateway associated to the sequence.
	 */
	@NotNull
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_gateway_id", nullable = false, unique = true)
	private PaymentGateway paymentGateway;

	public GenericSequence getGenericSequence() {
		return genericSequence;
	}

	public void setGenericSequence(GenericSequence genericSequence) {
		this.genericSequence = genericSequence;
	}

	public PaymentGateway getPaymentGateway() {
		return paymentGateway;
	}

	public void setPaymentGateway(PaymentGateway paymentGateway) {
		this.paymentGateway = paymentGateway;
	}

}
