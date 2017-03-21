package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;

import org.tmf.dsmapi.commons.Utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author bahman.barzideh
 * 
 *         { "percentage": "100%" }
 * 
 */
@JsonInclude(value = Include.NON_NULL)
public class AlterationPrice implements Serializable {
    private final static long serialVersionUID = 1L;

    String percentage;

    public AlterationPrice() {
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 89 * hash + (this.percentage != null ? this.percentage.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final AlterationPrice other = (AlterationPrice) object;
        if (Utilities.areEqual(this.percentage, other.percentage) == false) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "AlterationPrice{" + "percentage=" + percentage + '}';
    }

    public static AlterationPrice createProto() {
        AlterationPrice alterationPrice = new AlterationPrice();

        alterationPrice.percentage = "percentage";

        return alterationPrice;
    }

}
