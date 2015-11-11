package org.tmf.dsmapi.catalog.resource.product;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.commons.Utilities;

/**
 *
 * @author bahman.barzideh
 *
 * {
 *     "percentage": "100%"
 * }
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class AlterationPrice implements Serializable {
    private final static long serialVersionUID = 1L;

    private final static Logger logger = Logger.getLogger(ProductOffering.class.getName());

    @Column(name = "PRICE_ALT_PERCENTAGE", nullable = true)
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
