package org.meveo.model.quote;

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
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.offer.OfferComponent;

@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_quote_product", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_product_seq"), })
public class QuoteProduct extends BusinessEntity {

    
    /**
     * quote
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private Quote quote;

    /**
     * quote Version
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_version_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private QuoteVersion quoteVersion;

    /**
     * quote customer
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_customer_service_id", referencedColumnName = "id")
	@NotNull
    private QuoteCustomerService quoteCustomer;

    /**
     * offer component
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_component_id", referencedColumnName = "id", nullable = false)
	@NotNull
    private OfferComponent offerComponent;

    /**
     * product
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	@NotNull
    private Product product;

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    @NotNull
    private BigDecimal quantity = BigDecimal.ONE;

	/**
	 * @return the quote
	 */
	public Quote getQuote() {
		return quote;
	}

	/**
	 * @param quote the quote to set
	 */
	public void setQuote(Quote quote) {
		this.quote = quote;
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
	 * @return the quoteCustomer
	 */
	public QuoteCustomerService getQuoteCustomer() {
		return quoteCustomer;
	}

	/**
	 * @param quoteCustomer the quoteCustomer to set
	 */
	public void setQuoteCustomer(QuoteCustomerService quoteCustomer) {
		this.quoteCustomer = quoteCustomer;
	}

	/**
	 * @return the offerComponent
	 */
	public OfferComponent getOfferComponent() {
		return offerComponent;
	}

	/**
	 * @param offerComponent the offerComponent to set
	 */
	public void setOfferComponent(OfferComponent offerComponent) {
		this.offerComponent = offerComponent;
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
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
}
