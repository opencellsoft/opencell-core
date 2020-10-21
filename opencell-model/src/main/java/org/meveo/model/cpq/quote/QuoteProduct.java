package org.meveo.model.cpq.quote;

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
import org.meveo.model.quote.Quote;

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
}
