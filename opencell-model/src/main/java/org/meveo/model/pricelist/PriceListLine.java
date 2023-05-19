package org.meveo.model.pricelist;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * PriceList Line Entity.
 *
 * @author a.rouaguebe
 */
@Entity
@Table(name = "cat_price_list_line")
@GenericGenerator(
        name = "ID_GENERATOR",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "cat_price_list_line_seq")}
)
public class PriceListLine extends EnableBusinessCFEntity {

    /**
     * PriceList Line value used for Discount
     */
    @Column(name = "rate")
    private BigDecimal rate;

    /**
     * PriceList Line vamue for Fixed Price
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * Price list linked to this price list line
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_list_id")
    private PriceList priceList;

    /**
     * Offer category attached to this price line list
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_category_id")
    private OfferTemplateCategory offerCategory;

    /**
     * Offer Template attached to this price line list
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_template_id")
    private OfferTemplate offerTemplate;

    /**
     * Cpq Product attached to this price line list
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * Cpq ProductLine attached to this price line list
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_line_id")
    private ProductLine productCategory;

    /**
     * Charge Template attached to this price line list
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_template_id")
    private ChargeTemplate chargeTemplate;

    /**
     * PricePlan attached to this price line list
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_plan_id")
    private PricePlanMatrix pricePlan;

    /**
     * Price list Line Type (FIXED or PERCENTAGE)
     */
    @Column(name = "price_list_type")
    @Enumerated(EnumType.STRING)
    private PriceListTypeEnum priceListType;

    /**
     * Application Expression Language
     */
    @Column(name = "application_el")
    private String applicationEl;

    public BigDecimal getRate() {
        return rate;
    }

    public PriceListLine setRate(BigDecimal rate) {
        this.rate = rate;
        return this;
    }

    public PriceList getPriceList() {
        return priceList;
    }

    public PriceListLine setPriceList(PriceList priceList) {
        this.priceList = priceList;
        return this;
    }

    public OfferTemplateCategory getOfferCategory() {
        return offerCategory;
    }

    public PriceListLine setOfferCategory(OfferTemplateCategory offerCategory) {
        this.offerCategory = offerCategory;
        return this;
    }

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public PriceListLine setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public PriceListLine setProduct(Product product) {
        this.product = product;
        return this;
    }

    public ProductLine getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductLine productCategory) {
        this.productCategory = productCategory;
    }

    public ChargeTemplate getChargeTemplate() {
        return chargeTemplate;
    }

    public PriceListLine setChargeTemplate(ChargeTemplate chargeTemplate) {
        this.chargeTemplate = chargeTemplate;
        return this;
    }

    public PricePlanMatrix getPricePlan() {
        return pricePlan;
    }

    public PriceListLine setPricePlan(PricePlanMatrix pricePlan) {
        this.pricePlan = pricePlan;
        return this;
    }

    public PriceListTypeEnum getPriceListType() {
        return priceListType;
    }

    public PriceListLine setPriceListType(PriceListTypeEnum priceListType) {
        this.priceListType = priceListType;
        return this;
    }

    public String getApplicationEl() {
        return applicationEl;
    }

    public PriceListLine setApplicationEl(String applicationEl) {
        this.applicationEl = applicationEl;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
