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

package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.SubscriptionsListDto;
import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class SubscriptionsListResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "SubscriptionsListResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public final class SubscriptionsListResponseDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5980154480190489704L;

    /** The subscriptions. */
    private SubscriptionsListDto subscriptions;

    /**
     * Gets the subscriptions.
     *
     * @return the subscriptions
     */
    public SubscriptionsListDto getSubscriptions() {
        if (subscriptions == null) {
            subscriptions = new SubscriptionsListDto();
        }
        return subscriptions;
    }

    /**
     * Sets the subscriptions.
     *
     * @param subscriptions the new subscriptions
     */
    public void setSubscriptions(SubscriptionsListDto subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String toString() {
        return "ListSubscriptionResponseDto [subscriptions=" + subscriptions + ", toString()=" + super.toString() + "]";
    }
}