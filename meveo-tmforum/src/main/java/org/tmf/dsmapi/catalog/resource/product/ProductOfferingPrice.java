package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
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
 *     "name": "Monthly Price",
 *     "description": "monthlyprice",
 *     "validFor": {
 *         "startDateTime": "2013-04-19T16:42:23-04:00",
 *         "endDateTime": "2013-06-19T00:00:00-04:00"
 *     },
 *     "priceType": "recurring",
 *     "unitOfMeasure": "",
 *     "price": {
 *         "taxIncludedAmount": "12.00",
 *         "dutyFreeAmount": "10.00",
 *         "taxRate": "20.00",
 *         "currencyCode": "EUR",
 *         "percentage": 0
 *     },
 *     "recurringChargePeriod": "monthly",
 *      "productOfferPriceAlteration": {
 *          "name": "Shipping Discount",
 *          "description": "One time shipping discount",
 *          "validFor": {
 *              "startDateTime": "2013-04-19T16:42:23.0Z"
 *          },
 *          "priceType": "One Time discount",
 *          "unitOfMeasure": "",
 *          "price": {
 *              "percentage": 100
 *          },
 *          "recurringChargePeriod": "",
 *          "priceCondition": "apply if total amount of the  order is greater than 300.00"
 *      }
 * }
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class ProductOfferingPrice implements Serializable {
    private final static long serialVersionUID = 1L;

    @Column(name = "PRICE_NAME", nullable = true)
    @JsonProperty(value = "name")
    private String priceName;

    @Column(name = "PRICE_DESCRIPTION", nullable = true)
    @JsonProperty(value = "description")
    private String priceDescription;

    @AttributeOverrides({
        @AttributeOverride(name = "startDateTime", column = @Column(name = "PRICE_START_DATE_TIME")),
        @AttributeOverride(name = "endDateTime", column = @Column(name = "PRICE_END_DATE_TIME"))
    })
    @JsonProperty(value = "validFor")
    private TimeRange priceValidFor;

    @Column(name = "PRICE_TYPE", nullable = true)
    private ProductOfferingPriceType priceType;

    @Column(name = "UNIT_OF_MEASURE", nullable = true)
    private String unitOfMeasure;

    @Embedded
    private Price price;

    @Column(name = "RECURRING_CHARGE_PERIOD", nullable = true)
    private String recurringChargePeriod;

    @Embedded
    private ProductOfferPriceAlteration productOfferPriceAlteration;
    
    public ProductOfferingPrice() {
    }

    public String getPriceName() {
        return priceName;
    }

    public void setPriceName(String priceName) {
        this.priceName = priceName;
    }

    public String getPriceDescription() {
        return priceDescription;
    }

    public void setPriceDescription(String priceDescription) {
        this.priceDescription = priceDescription;
    }

    public TimeRange getPriceValidFor() {
        return priceValidFor;
    }

    public void setPriceValidFor(TimeRange priceValidFor) {
        this.priceValidFor = priceValidFor;
    }

    public ProductOfferingPriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(ProductOfferingPriceType priceType) {
        this.priceType = priceType;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getRecurringChargePeriod() {
        return recurringChargePeriod;
    }

    public void setRecurringChargePeriod(String recurringChargePeriod) {
        this.recurringChargePeriod = recurringChargePeriod;
    }

    public ProductOfferPriceAlteration getProductOfferPriceAlteration() {
        return productOfferPriceAlteration;
    }

    public void setProductOfferPriceAlteration(ProductOfferPriceAlteration productOfferPriceAlteration) {
        this.productOfferPriceAlteration = productOfferPriceAlteration;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 37 * hash + (this.priceName != null ? this.priceName.hashCode() : 0);
        hash = 37 * hash + (this.priceDescription != null ? this.priceDescription.hashCode() : 0);
        hash = 37 * hash + (this.priceValidFor != null ? this.priceValidFor.hashCode() : 0);
        hash = 37 * hash + (this.priceType != null ? this.priceType.hashCode() : 0);
        hash = 37 * hash + (this.unitOfMeasure != null ? this.unitOfMeasure.hashCode() : 0);
        hash = 37 * hash + (this.price != null ? this.price.hashCode() : 0);
        hash = 37 * hash + (this.recurringChargePeriod != null ? this.recurringChargePeriod.hashCode() : 0);
        hash = 37 * hash + (this.productOfferPriceAlteration != null ? this.productOfferPriceAlteration.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final ProductOfferingPrice other = (ProductOfferingPrice) object;
        if (Utilities.areEqual(this.priceName, other.priceName) == false) {
            return false;
        }

        if (Utilities.areEqual(this.priceDescription, other.priceDescription) == false) {
            return false;
        }

        if (Utilities.areEqual(this.priceValidFor, other.priceValidFor) == false) {
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

        if (Utilities.areEqual(this.productOfferPriceAlteration, other.productOfferPriceAlteration) == false) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "ProductOfferingPrice{" + "priceName=" + priceName + ", priceDescription=" + priceDescription + ", priceValidFor=" + priceValidFor + ", priceType=" + priceType + ", unitOfMeasure=" + unitOfMeasure + ", price=" + price + ", recurringChargePeriod=" + recurringChargePeriod + ", productOfferPriceAlteration=" + productOfferPriceAlteration + '}';
    }

    public static ProductOfferingPrice createProto() {
        ProductOfferingPrice productOfferingPrice = new ProductOfferingPrice();

        productOfferingPrice.priceName = "name";
        productOfferingPrice.priceDescription = "description";
        productOfferingPrice.priceValidFor = TimeRange.createProto ();

        productOfferingPrice.priceType = ProductOfferingPriceType.RECURRING;
        productOfferingPrice.unitOfMeasure = "unit of measure";
        productOfferingPrice.price = Price.createProto();
        productOfferingPrice.recurringChargePeriod = "recurring charge period";
        productOfferingPrice.productOfferPriceAlteration = ProductOfferPriceAlteration.createProto();
        
        return productOfferingPrice;
    }

}
