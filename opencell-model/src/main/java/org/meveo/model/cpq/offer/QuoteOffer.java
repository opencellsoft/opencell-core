package org.meveo.model.cpq.offer;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.AuditableEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.quote.QuoteCustomerService;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;


@Entity
@Table(name="quote_offer")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "quote_offer_seq"), })
public class QuoteOffer extends AuditableEntity {


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
	private QuoteCustomerService quoteCustomerService;
	

    @OneToMany(mappedBy = "offerQuote", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
	private List<QuoteProduct> quoteProduct;
	
	private int sequence;

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
	 * @return the quoteCustomerService
	 */
	public QuoteCustomerService getQuoteCustomerService() {
		return quoteCustomerService;
	}

	/**
	 * @param quoteCustomerService the quoteCustomerService to set
	 */
	public void setQuoteCustomerService(QuoteCustomerService quoteCustomerService) {
		this.quoteCustomerService = quoteCustomerService;
	}

	/**
	 * @return the quoteProduct
	 */
	public List<QuoteProduct> getQuoteProduct() {
		return quoteProduct;
	}

	/**
	 * @param quoteProduct the quoteProduct to set
	 */
	public void setQuoteProduct(List<QuoteProduct> quoteProduct) {
		this.quoteProduct = quoteProduct;
	}

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
