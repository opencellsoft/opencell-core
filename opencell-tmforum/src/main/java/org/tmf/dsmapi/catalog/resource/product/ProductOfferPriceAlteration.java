package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author bahman.barzideh
 * 
 *         { "name": "Shipping Discount", "description": "One time shipping discount", "validFor": { "startDateTime": "2013-04-19T16:42:23-04:00", }, "priceType":
 *         "One Time discount", "unitOfMeasure": "", "price": { "percentage": "100%" }, "recurringChargePeriod": "", "priceCondition":
 *         "apply if total amount of the  order is greater than 300.00" }
 * 
 */
@JsonInclude(value = Include.NON_NULL)
@XmlRootElement(name="ProductOfferPriceAlteration", namespace="http://www.tmforum.org")
@XmlType(name="ProductOfferPriceAlteration", namespace="http://www.tmforum.org")
public class ProductOfferPriceAlteration implements Serializable {
    private final static long serialVersionUID = 1L;

    private String name;

    private String description;

    private TimeRange validFor;

    private ProductOfferPriceAlterationType priceType;

    private String unitOfMeasure;

    private AlterationPrice price;

    private String recurringChargePeriod;

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
        return "ProductOfferPriceAlteration{" + "name=" + name + ", description=" + description + ", validFor=" + validFor + ", priceType=" + priceType + ", unitOfMeasure="
                + unitOfMeasure + ", price=" + price + ", recurringChargePeriod=" + recurringChargePeriod + ", priceCondition=" + priceCondition + '}';
    }
}