package org.meveo.model.cpq.contract;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;

/**
 * @author Tarik FAKHOURI
 * @author Mbarek-Ay
 * @version 11.0
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "ContractItem")
@Table(name = "cpq_contract_item", uniqueConstraints = { @UniqueConstraint(columnNames = {"code"})})
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_contract_item_seq")})
@NamedQueries({
	@NamedQuery(name = "ContractItem.getApplicableContracts", query = "select c from ContractItem c left join c.targetAccountingArticles article where  c.contract.id=:contractId "
			+ " and (c.offerTemplate is null or c.offerTemplate.id=:offerId) "
			+ " and (c.product is null or c.product.id=:productId) "
			+ " and (c.chargeTemplate is null or c.chargeTemplate.id=:chargeTemplateId) "
			+ " and ((c.targetAccountingArticles is empty and c.chargeTemplate is not null) or article.id =:accountingArticleId)",
			hints = { @QueryHint(name = "org.hibernate.cacheable", value = "true"), @QueryHint(name = "org.hibernate.readOnly", value = "true") } )})
	
public class ContractItem extends EnableBusinessCFEntity {

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
	private Double rate;

    /**
     * Amount without tax
     */
    @Column(name = "amount_without_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal amountWithoutTax;

    /**
     * rate type
     */
	@Enumerated(EnumType.STRING)
	@Column(name = "rate_type", length = 50)
	private ContractRateTypeEnum contractRateType = ContractRateTypeEnum.PERCENTAGE;
	
	/**
	 * Shall discount be created as a separate WO
	 */
	@Type(type = "numeric_boolean")
	@Column(name = "separate_discount")
	private boolean separateDiscount = false;

	@Column(name = "application_el", length = 2000)
	@Size(max = 2000)
	private String applicationEl;
	
	/**
     * list of trading contract item attached
     */
	@OneToMany(mappedBy = "contractItem", fetch = FetchType.LAZY)
    private Set<TradingContractItem> tradingContractItems = new HashSet<>();
	
	/**
	 * Flag applicable On Overridden Price
	 */
	@Type(type = "numeric_boolean")
	@Column(name = "applicable_on_overridden_price")
	private boolean applicableOnOverriddenPrice = false;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "contract_item_articles", joinColumns = @JoinColumn(name = "contract_item_id"), inverseJoinColumns = @JoinColumn(name = "accounting_article_id"))
	private Set<AccountingArticle> targetAccountingArticles = new HashSet<>();
	
	/**
	 * @return the contract
	 */
	public Contract getContract() {
		return contract;
	}

	/**
	 * @param contract the contract to set
	 */
	public void setContract(Contract contract) {
		this.contract = contract;
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
	public Double getRate() {
		return rate;
	}

	/**
	 * @param rate the rate to set
	 */
	public void setRate(Double rate) {
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
	 
	public ContractRateTypeEnum getContractRateType() {
		return contractRateType;
	}

	public void setContractRateType(ContractRateTypeEnum contractRateType) {
		this.contractRateType = contractRateType;
	}
	
	public String getApplicationEl() {
		return applicationEl;
	}

	public void setApplicationEl(String applicationEl) {
		this.applicationEl = applicationEl;
	}

	/**
	 * @return Shall discount be created as a separate WO
	 */
	public boolean isSeparateDiscount() {
		return separateDiscount;
	}

    /**
     * @param separateDiscount Shall discount be created as a separate WO
     */
	public void setSeparateDiscount(boolean separateDiscount) {
		this.separateDiscount = separateDiscount;
	}

	public Set<TradingContractItem> getTradingContractItems() {
		return tradingContractItems;
	}

	public void setTradingContractItems(Set<TradingContractItem> tradingContractItems) {
		this.tradingContractItems = tradingContractItems;
	}

	public boolean isApplicableOnOverriddenPrice() {
		return applicableOnOverriddenPrice;
	}

	public void setApplicableOnOverriddenPrice(boolean applicableOnOverriddenPrice) {
		this.applicableOnOverriddenPrice = applicableOnOverriddenPrice;
	}
	
	public Set<AccountingArticle> getTargetAccountingArticles() {
		return targetAccountingArticles;
	}
	
	public void setTargetAccountingArticles(Set<AccountingArticle> targetAccountingArticles) {
		this.targetAccountingArticles = targetAccountingArticles;
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
