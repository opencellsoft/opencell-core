package org.meveo.model.billing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.meveo.commons.utils.CustomDateSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * Encapsulates a SubscriptionRenewal value.
 * 
 * @author Abdellatif BARI
 * @since 7.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Renewal implements Serializable, Cloneable {

    /**
     * A date till which subscription is subscribed. After this date it will either be extended or terminated
     */
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date subscribedTillDate;

    /**
     * Subscription renewal
     */
    @JsonUnwrapped()
    private SubscriptionRenewal value;

    /**
     * Renewal instance
     */
    public Renewal() {
    }

    /**
     * Instantiate Renewal with a given value
     *
     * @param value Renewal to assign
     */
    public Renewal(SubscriptionRenewal value, Date subscribedTillDate) {
        this.value = value;
        this.subscribedTillDate = subscribedTillDate;
    }

    /**
     * Gets the subscribed till date
     *
     * @return the subscribed till date
     */
    public Date getSubscribedTillDate() {
        return subscribedTillDate;
    }

    /**
     * Sets the subscribed till date.
     *
     * @param subscribedTillDate the new subscribed till date
     */
    public void setSubscribedTillDate(Date subscribedTillDate) {
        this.subscribedTillDate = subscribedTillDate;
    }

    /**
     * Gets the value
     *
     * @return the value
     */
    public SubscriptionRenewal getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(SubscriptionRenewal value) {
        this.value = value;
    }
}