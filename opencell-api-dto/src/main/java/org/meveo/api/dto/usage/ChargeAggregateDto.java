package org.meveo.api.dto.usage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * The Class ChargeAggregateDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "ChargeAggregate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeAggregateDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The description. */
    private String description;
    
    /** The quantity. */
    private String quantity;
    
    /** The amount. */
    private String amount;

    /**
     * Instantiates a new charge aggregate dto.
     */
    public ChargeAggregateDto() {

    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity the quantity to set
     */
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }
}