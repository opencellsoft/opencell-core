package org.meveo.model.cpq.contract;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.attribute.ProductAttribute;
import org.meveo.model.cpq.offer.CommercialOffer;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@Entity
@Table(name = "cpq_contract_item")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_contract_item_seq")})
public class ContractItem extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5080807655628692787L;
	
	/**
	 * contract
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_contract_id")
	private Contract contarct;

	/**
	 * commercial offer
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_commercial_offer_id")
	private CommercialOffer commercialOffer;

	/**
	 * product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_product_id")
	private Product product;

	/**
	 * product attribute
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cpq_product_attribute_id", nullable = false)
	@NotNull
	private ProductAttribute productAttribute;

	/**
	 * price plan
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "price_plan_id", nullable = false)
	@NotNull
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
	public Contract getContarct() {
		return contarct;
	}


	/**
	 * @param contarct the contarct to set
	 */
	public void setContarct(Contract contarct) {
		this.contarct = contarct;
	}


	/**
	 * @return the commercialOffer
	 */
	public CommercialOffer getCommercialOffer() {
		return commercialOffer;
	}


	/**
	 * @param commercialOffer the commercialOffer to set
	 */
	public void setCommercialOffer(CommercialOffer commercialOffer) {
		this.commercialOffer = commercialOffer;
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
	 * @return the productAttribute
	 */
	public ProductAttribute getProductAttribute() {
		return productAttribute;
	}


	/**
	 * @param productAttribute the productAttribute to set
	 */
	public void setProductAttribute(ProductAttribute productAttribute) {
		this.productAttribute = productAttribute;
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
		result = prime * result + Objects.hash(amountWithoutTax, chargeTemplate, commercialOffer, contarct, pricePlan,
				product, productAttribute, rate, serviceTemplate);
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
				&& Objects.equals(commercialOffer, other.commercialOffer) && Objects.equals(contarct, other.contarct)
				&& Objects.equals(pricePlan, other.pricePlan) && Objects.equals(product, other.product)
				&& Objects.equals(productAttribute, other.productAttribute) && rate == other.rate
				&& Objects.equals(serviceTemplate, other.serviceTemplate);
	}

    
	
}
