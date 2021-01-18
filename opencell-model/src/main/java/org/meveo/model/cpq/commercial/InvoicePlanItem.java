package org.meveo.model.cpq.commercial;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;


/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_invoicing_plan_item", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_invoicing_plan_item_seq")})
public class InvoicePlanItem extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8433284624540252795L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_plan_id", nullable = false)
	@NotNull
	private InvoicingPlan billingPlan;
	
	@Column(name = "advancement", length = 20, nullable = false)
	@NotNull
	private String advancement;

	@Column(name = "rate_to_bill",nullable = false, precision = NB_PRECISION, scale = NB_DECIMALS)
	@NotNull
	private BigDecimal rateToBill;
	
}
