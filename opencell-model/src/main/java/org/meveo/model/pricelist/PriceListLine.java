package org.meveo.model.pricelist;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.cpq.Product;

import javax.persistence.*;
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

    private BigDecimal rate;

    @ManyToOne
    @JoinColumn(name = "price_list_id")
    private PriceList priceList;

    @ManyToOne
    @JoinColumn(name = "offer_category_id")
    private OfferTemplateCategory offerCategory;

    @ManyToOne
    @JoinColumn(name = "offer_template_id")
    private OfferTemplate offerTemplate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "charge_template_id")
    private ChargeTemplate chargeTemplate;

    @ManyToOne
    @JoinColumn(name = "price_plan_id")
    private PricePlanMatrix pricePlan;

    @Column(name = "price_list_type")
    @Enumerated(EnumType.STRING)
    private PriceListTypeEnum priceListType;

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
}
