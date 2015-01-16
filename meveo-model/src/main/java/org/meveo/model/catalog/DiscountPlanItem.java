package org.meveo.model.catalog;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "CAT_DISCOUNT_PLAN_ITEM")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_DISCOUNT_PLAN_ITEM_SEQ")
public class DiscountPlanItem extends AuditableEntity {

	private static final long serialVersionUID = 4543503736567841084L;

	@ManyToOne
	@JoinColumn(name = "DISCOUNT_PLAN_ID", nullable = false)
	private DiscountPlan discountPlan;

	@ManyToOne
	@JoinColumn(name = "OFFER_TEMPLATE_ID")
	private OfferTemplate offerTemplate;

	@ManyToOne
	@JoinColumn(name = "INVOICE_CATEGORY_ID")
	private InvoiceCategory invoiceCategory;

	@ManyToOne
	@JoinColumn(name = "INVOICE_SUB_CATEGORY_ID")
	private InvoiceSubCategory invoiceSubCategory;

	@ManyToOne
	@JoinColumn(name = "CHARGE_TEMPLATE_ID")
	private ChargeTemplate chargeTemplate;

	@Column(name = "DISCOUNT_PERCENT", precision = NB_PRECISION, scale = NB_DECIMALS)
	@Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
	@Min(0)
	@Max(100)
	private BigDecimal percent;

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	public InvoiceCategory getInvoiceCategory() {
		return invoiceCategory;
	}

	public void setInvoiceCategory(InvoiceCategory invoiceCategory) {
		this.invoiceCategory = invoiceCategory;
	}

	public InvoiceSubCategory getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	public ChargeTemplate getChargeTemplate() {
		return chargeTemplate;
	}

	public void setChargeTemplate(ChargeTemplate chargeTemplate) {
		this.chargeTemplate = chargeTemplate;
	}

	public BigDecimal getPercent() {
		return percent;
	}

	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}

}
