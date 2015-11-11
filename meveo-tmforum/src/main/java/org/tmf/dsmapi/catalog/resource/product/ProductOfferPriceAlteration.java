package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author bahman.barzideh
 *
 * {
 *     "name": "Shipping Discount",
 *     "description": "One time shipping discount",
 *     "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00",
 *     },
 *     "priceType": "One Time discount",
 *     "unitOfMeasure": "",
 *     "price": {
 *         "percentage": "100%"
 *     },
 *     "recurringChargePeriod": "",
 *     "priceCondition": "apply if total amount of the  order is greater than 300.00"
 * }
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class ProductOfferPriceAlteration implements Serializable {
    private final static long serialVersionUID = 1L;

    private final static Logger logger = Logger.getLogger(ProductOffering.class.getName());

    @Column(name = "PRICE_ALT_NAME", nullable = true)
    private String name;

    @Column(name = "PRICE_ALT_DESCRIPTION", nullable = true)
    private String description;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "startDateTime", column = @Column(name = "PRICE_ALT_START_DATE_TIME")),
        @AttributeOverride(name = "endDateTime", column = @Column(name = "PRICE_ALT_END_DATE_TIME"))
    })
    private TimeRange validFor;

    @Column(name = "PRICE_ALT_PRICE_TYPE", nullable = true)
    private ProductOfferPriceAlterationType priceType;

    @Column(name = "PRICE_ALT_UNIT_OF_MEASURE", nullable = true)
    private String unitOfMeasure;

    @Embedded
    private AlterationPrice price;

    @Column(name = "PRICE_ALT_RECURRING_CHARGE_PERIOD", nullable = true)
    private String recurringChargePeriod;

    @Column(name = "PRICE_ALT_PRICE_CONDITION", nullable = true)
    private String priceCondition;

    public ProductOfferPriceAlteration() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TimeRange getValidFor() {
        return validFor;
    }

    public void setValidFor(TimeRange validFor) {
        this.validFor = validFor;
    }

    public ProductOfferPriceAlterationType getPriceType() {
        return priceType;
    }

    public void setPriceType(ProductOfferPriceAlterationType priceType) {
        this.priceType = priceType;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public AlterationPrice getPrice() {
        return price;
    }

    public void setPrice(AlterationPrice price) {
        this.price = price;
    }

    public String getRecurringChargePeriod() {
        return recurringChargePeriod;
    }

    public void setRecurringChargePeriod(String recurringChargePeriod) {
        this.recurringChargePeriod = recurringChargePeriod;
    }

    public String getPriceCondition() {
        return priceCondition;
    }

    public void setPriceCondition(String priceCondition) {
        this.priceCondition = priceCondition;
    }

    @JsonProperty(value = "validFor")
    public TimeRange validForToJson() {
        return (validFor != null && validFor.isEmpty() == false) ? validFor : null;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 61 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 61 * hash + (this.validFor != null ? this.validFor.hashCode() : 0);
        hash = 61 * hash + (this.priceType != null ? this.priceType.hashCode() : 0);
        hash = 61 * hash + (this.unitOfMeasure != null ? this.unitOfMeasure.hashCode() : 0);
        hash = 61 * hash + (this.price != null ? this.price.hashCode() : 0);
        hash = 61 * hash + (this.recurringChargePeriod != null ? this.recurringChargePeriod.hashCode() : 0);
        hash = 61 * hash + (this.priceCondition != null ? this.priceCondition.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final ProductOfferPriceAlteration other = (ProductOfferPriceAlteration) object;
        if (Utilities.areEqual(this.name, other.name) == false) {
            return false;
        }

        if (Utilities.areEqual(this.description, other.description) == false) {
            return false;
        }

        if (Utilities.areEqual(this.validFor, other.validFor) == false) {
            return false;
        }

        if (this.priceType != other.priceType) {
            return false;
        }

        if (Utilities.areEqual(this.unitOfMeasure, other.unitOfMeasure) == false) {
            return false;
        }

        if (Utilities.areEqual(this.price, other.price) == false) {
            return false;
        }

        if (Utilities.areEqual(this.recurringChargePeriod, other.recurringChargePeriod) == false) {
            return false;
        }

        if (Utilities.areEqual(this.priceCondition, other.priceCondition) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ProductOfferPriceAlteration{" + "name=" + name + ", description=" + description + ", validFor=" + validFor + ", priceType=" + priceType + ", unitOfMeasure=" + unitOfMeasure + ", price=" + price + ", recurringChargePeriod=" + recurringChargePeriod + ", priceCondition=" + priceCondition + '}';
    }

    public static ProductOfferPriceAlteration createProto() {
        ProductOfferPriceAlteration productOfferPriceAlteration = new ProductOfferPriceAlteration();

        productOfferPriceAlteration.name = "name";
        productOfferPriceAlteration.description = "description";
        productOfferPriceAlteration.validFor = TimeRange.createProto();
        productOfferPriceAlteration.priceType = ProductOfferPriceAlterationType.RECURRING;
        productOfferPriceAlteration.unitOfMeasure = "unit of measure";
        productOfferPriceAlteration.price = AlterationPrice.createProto();
        productOfferPriceAlteration.recurringChargePeriod = "recurring charge period";
        productOfferPriceAlteration.priceCondition = "price condition";

        return productOfferPriceAlteration;
    }

}
