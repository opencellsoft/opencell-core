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
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;


/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "InvoicingPlanItems")
@Table(name = "cpq_invoicing_plan_item", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_invoicing_plan_item_seq")})
public class InvoicingPlanItem extends EnableBusinessCFEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8433284624540252795L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_plan_id", nullable = false)
	@NotNull
	private InvoicingPlan billingPlan;
	
	@Column(name = "advancement", nullable = false)
	@NotNull
	private Integer advancement;
	
	@Column(name = "rate_to_bill",nullable = false, precision = NB_PRECISION, scale = NB_DECIMALS)
	@NotNull
	private BigDecimal rateToBill;

	/**
	 * @return the billingPlan
	 */
	public InvoicingPlan getBillingPlan() {
		return billingPlan;
	}

	/**
	 * @param billingPlan the billingPlan to set
	 */
	public void setBillingPlan(InvoicingPlan billingPlan) {
		this.billingPlan = billingPlan;
	}

	/**
	 * @return the advancement
	 */
	public Integer getAdvancement() {
		return advancement;
	}

	/**
	 * @param advancement the advancement to set
	 */
	public void setAdvancement(Integer advancement) {
		this.advancement = advancement;
	}

	/**
	 * @return the rateToBill
	 */
	public BigDecimal getRateToBill() {
		return rateToBill;
	}

	/**
	 * @param rateToBill the rateToBill to set
	 */
	public void setRateToBill(BigDecimal rateToBill) {
		this.rateToBill = rateToBill;
	}
	
}
