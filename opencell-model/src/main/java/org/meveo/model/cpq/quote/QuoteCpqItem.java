package org.meveo.model.cpq.quote;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.cpq.offer.OfferComponent;

@Embeddable
public class QuoteCpqItem {


    /**
     * Quote version
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_version_id", nullable = false, updatable = false)
    @NotNull
    private QuoteVersion quoteVersion;
    

    /**
     * customer service
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_customer_service_id", updatable = false)
    private QuoteCustomerService customerService;
    

    /**
     * quote product
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_product_id", updatable = false)
    private QuoteProduct quoteProduct;

    /**
     * offer component
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_component_id", nullable = false, updatable = false)
    @NotNull
    private OfferComponent offerComponent;
    
    @Column(name = "service_code", nullable = false, length = 20)
    @Size(max = 20)
    private String serviceCode;

    /**
     * Quantity subscribed
     */
    @Column(name = "quantity", precision = 23, scale = 12, nullable = false)
    @NotNull
    private BigDecimal quantity = BigDecimal.ONE;
    
    /**
     * service type
     */
    @Column(name = "service_type", nullable = false)
    private Integer serviceType;
    
    /**
     * value
     */
    @Column(name = "value")
    private String value;
    
    /**
     * OS_UNIT_PRICE_WITHOUT_TAX
     */
    @Column(name = "os_unite_price_without_tax", precision = 23, scale = 12)
    private BigDecimal osUnitPriceWithoutTax;

    /**
     * OS_PRICE_WITHOUT_TAX
     */
    @Column(name = "os_price_without_tax", precision = 23, scale = 12)
    private BigDecimal osPriceWithoutTax;
    
    /**
     * OS_TAX_CODE
     */
    @Column(name = "os_tax_code", precision = 23, scale = 12)
    private BigDecimal osTAxCode;
    
    /**
     * OS_TAX_RATE
     */
    @Column(name = "os_tax_rate")
    private int osTAxRate;
    
    /**
     * OS_PRICE_WITH_TAX
     */
    @Column(name = "os_price_with_tax", precision = 23, scale = 12)
    private BigDecimal osPriceWithTax;

    /**
     * RECURRENCE_DURATION
     */
    @Column(name = "recurrence_duration")
    private int recurrenceDuration;
    /**
     * RECURRENCE_PERIODICITY
     */
    @Column(name = "recurrence_periodicity")
    private int recurrencePeriodicity;
    

    /**
     * RC_UNIT_PRICE_WITHOUT_TAX
     */
    @Column(name = "rc_unite_price_without_tax", precision = 23, scale = 12)
    private BigDecimal rcUnitPriceWithoutTax;

    /**
     * RC_PRICE_WITHOUT_TAX
     */
    @Column(name = "rc_price_without_tax", precision = 23, scale = 12)
    private BigDecimal rcPriceWithoutTax;
    
    /**
     * RC_TAX_CODE
     */
    @Column(name = "rc_tax_code", precision = 23, scale = 12)
    private BigDecimal rcTAxCode;
    
    /**
     * RC_TAX_RATE
     */
    @Column(name = "rc_tax_rate")
    private int rcTAxRate;
    
    /**
     * RC_PRICE_WITH_TAX
     */
    @Column(name = "rc_price_with_tax", precision = 23, scale = 12)
    private BigDecimal rcPriceWithTax;
    
}
