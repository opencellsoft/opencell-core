package org.meveo.model.cpq.contract;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@Entity
@Table(name = "cpq_contract_item", uniqueConstraints = { @UniqueConstraint(columnNames = {"code"})})
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_contract_item_seq")})
public class ContractItem extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5080807655628692787L;
	
	/**
	 * contract
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_contract_id", nullable = false)
	private Contract contract;

	/**
	 * commercial offer
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_template_id")
	private OfferTemplate offerTemplate;

	/**
	 * product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_product_id")
	private Product product;

	/**
	 * price plan
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "price_plan_id")
	private PricePlanMatrix pricePlan;

	/**
	 * charge template 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "charge_template_id")
	private ChargeTemplate chargeTemplate;

	/**
	 * service template
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_template_id")
	private ServiceTemplate serviceTemplate;

	/**
	 * rate 
	 */
	@Column(name = "rate")
	private int rate;
	

    /**
     * Amount without tax
     */
    @Column(name = "amount_without_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal amountWithoutTax;


	/**
	 * @return the contarct
	 */
	public Contract getContract() {
		return contract;
	}


	/**
	 * @param contarct the contarct to set
	 */
	public void setContract(Contract contarct) {
		this.contract = contarct;
	}


	/**
	 * @return the commercialOffer
	 */
	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}


	/**
	 * @param offerTemplate the commercialOffer to set
	 */
	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}


	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}


	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}


	/**
	 * @return the pricePlan
	 */
	public PricePlanMatrix getPricePlan() {
		return pricePlan;
	}


	/**
	 * @param pricePlan the pricePlan to set
	 */
	public void setPricePlan(PricePlanMatrix pricePlan) {
		this.pricePlan = pricePlan;
	}


	/**
	 * @return the chargeTemplate
	 */
	public ChargeTemplate getChargeTemplate() {
		return chargeTemplate;
	}


	/**
	 * @param chargeTemplate the chargeTemplate to set
	 */
	public void setChargeTemplate(ChargeTemplate chargeTemplate) {
		this.chargeTemplate = chargeTemplate;
	}


	/**
	 * @return the serviceTemplate
	 */
	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}


	/**
	 * @param serviceTemplate the serviceTemplate to set
	 */
	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}


	/**
	 * @return the rate
	 */
	public int getRate() {
		return rate;
	}


	/**
	 * @param rate the rate to set
	 */
	public void setRate(int rate) {
		this.rate = rate;
	}


	/**
	 * @return the amountWithoutTax
	 */
	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}


	/**
	 * @param amountWithoutTax the amountWithoutTax to set
	 */
	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(amountWithoutTax, chargeTemplate, offerTemplate, contract, pricePlan,
				product, rate, serviceTemplate);
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContractItem other = (ContractItem) obj;
		return Objects.equals(amountWithoutTax, other.amountWithoutTax)
				&& Objects.equals(chargeTemplate, other.chargeTemplate)
				&& Objects.equals(offerTemplate, other.offerTemplate) && Objects.equals(contract, other.contract)
				&& Objects.equals(pricePlan, other.pricePlan) && Objects.equals(product, other.product)
			    && rate == other.rate
				&& Objects.equals(serviceTemplate, other.serviceTemplate);
	}

    
	
}
