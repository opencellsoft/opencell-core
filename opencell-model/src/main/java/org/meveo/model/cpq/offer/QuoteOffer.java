package org.meveo.model.cpq.offer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.AuditableCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.quote.QuoteLot;
import org.meveo.model.quote.QuotePrice;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;


@Entity
@CustomFieldEntity(cftCodePrefix = "QuoteOffer")
@Table(name="quote_offer")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "quote_offer_seq"), })
@NamedQueries({
		@NamedQuery(name = "QuoteOffer.findByTemplateAndQuoteVersion", query = "select q from QuoteOffer q left join q.offerTemplate qo left join q.quoteVersion qq where qo.code=:offerTemplateCode and qq.quote.code=:cpqQuoteCode and qq.quoteVersion=:quoteVersion"),
		@NamedQuery(name = "QuoteOffer.findQuoteAttribute", query = "select qf from QuoteOffer qf left join qf.quoteVersion qv  " + " where qv.id=:quoteVersionId and qf.offerTemplate.code=:offerCode")
})
public class QuoteOffer extends AuditableCFEntity {


	public QuoteOffer() {
	}


	public QuoteOffer(QuoteOffer copy) {
		this.offerTemplate = copy.offerTemplate;
		this.billableAccount = copy.billableAccount;
		this.quoteVersion = copy.quoteVersion;
		this.quoteLot = copy.quoteLot;
		this.quoteProduct = copy.quoteProduct;
		this.contractCode = copy.contractCode;
		this.position = copy.position;
		this.cfValues = copy.getCfValues();
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 8394446810964282119L;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_template_id", nullable = false)
	@NotNull
	private OfferTemplate offerTemplate;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billable_account_id")
	private BillingAccount billableAccount;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_version_id", nullable = false)
	@NotNull
	private QuoteVersion quoteVersion;
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_customer_service_id")
	private QuoteLot quoteLot;
	

    @OneToMany(mappedBy = "quoteOffer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteProduct> quoteProduct;
    
    
    @OneToMany(mappedBy = "quoteOffer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteAttribute> quoteAttributes = new ArrayList<QuoteAttribute>();
    /**
	 * opportunityRef
	 */
	@Column(name = "contract_code", length = 50)
	@Size(max = 50)
	private String contractCode;
	
	/**
	 * opportunityRef
	 */
	@Column(name = "position")
	private Integer position;

	/**
	 * discountPlan attached to this quoteOffer
	 */
    @ManyToOne
	@JoinColumn(name = "discount_plan_id", referencedColumnName = "id")
	private DiscountPlan discountPlan;
    
    @Column(name = "sequence")
    protected Integer sequence=0;
    
    
    @OneToMany(mappedBy = "quoteOffer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuotePrice> quotePrices = new ArrayList<QuotePrice>();

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}
	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}
	
	/**
	 * @return the offerTemplate
	 */
	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	/**
	 * @param offerTemplate the offerTemplate to set
	 */
	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	/**
	 * @return the billableAccount
	 */
	public BillingAccount getBillableAccount() {
		return billableAccount;
	}

	/**
	 * @param billableAccount the billableAccount to set
	 */
	public void setBillableAccount(BillingAccount billableAccount) {
		this.billableAccount = billableAccount;
	}

	/**
	 * @return the quoteVersion
	 */
	public QuoteVersion getQuoteVersion() {
		return quoteVersion;
	}

	/**
	 * @param quoteVersion the quoteVersion to set
	 */
	public void setQuoteVersion(QuoteVersion quoteVersion) {
		this.quoteVersion = quoteVersion;
	}


	/**
	 * @return the quoteProduct
	 */
	public List<QuoteProduct> getQuoteProduct() {
		if(quoteProduct == null)
			quoteProduct = new ArrayList<>();
		return quoteProduct;
	}

	/**
	 * @param quoteProduct the quoteProduct to set
	 */
	public void setQuoteProduct(List<QuoteProduct> quoteProduct) {
		this.quoteProduct = quoteProduct;
	}



	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the quoteLot
	 */
	public QuoteLot getQuoteLot() {
		return quoteLot;
	}

	/**
	 * @param quoteLot the quoteLot to set
	 */
	public void setQuoteLot(QuoteLot quoteLot) {
		this.quoteLot = quoteLot;
	}

	/**
	 * @return the contractCode
	 */
	public String getContractCode() {
		return contractCode;
	}

	/**
	 * @param contractCode the contractCode to set
	 */
	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	/**
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}
	
	

	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}


	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	

	/**
	 * @return the quoteAttributes
	 */
	public List<QuoteAttribute> getQuoteAttributes() {
		return quoteAttributes;
	}


	/**
	 * @param quoteAttributes the quoteAttributes to set
	 */
	public void setQuoteAttributes(List<QuoteAttribute> quoteAttributes) {
		this.quoteAttributes = quoteAttributes;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(billableAccount, offerTemplate, quoteLot, quoteProduct, quoteVersion);
		return result;
	}
	
	

	public List<QuotePrice> getQuotePrices() {
		return quotePrices;
	}


	public void setQuotePrices(List<QuotePrice> quotePrices) {
		this.quotePrices = quotePrices;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		QuoteOffer other = (QuoteOffer) obj;
		return Objects.equals(id, other.id);
	}
	
	

}
