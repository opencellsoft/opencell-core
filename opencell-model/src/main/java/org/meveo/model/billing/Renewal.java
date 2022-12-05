/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.billing;

import java.io.Serializable;
import java.util.Date;

import org.meveo.commons.utils.CustomDateSerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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