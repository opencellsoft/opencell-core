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

package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ServiceChargeTemplateSubscriptionsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateSubscriptionsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8759911503295449204L;

    /** The service charge template subscription. */
    private List<ServiceChargeTemplateSubscriptionDto> serviceChargeTemplateSubscription;

    /**
     * Gets the service charge template subscription.
     *
     * @return the service charge template subscription
     */
    public List<ServiceChargeTemplateSubscriptionDto> getServiceChargeTemplateSubscription() {
        if (serviceChargeTemplateSubscription == null)
            serviceChargeTemplateSubscription = new ArrayList<ServiceChargeTemplateSubscriptionDto>();
        return serviceChargeTemplateSubscription;
    }

    /**
     * Sets the service charge template subscription.
     *
     * @param serviceChargeTemplateSubscription the new service charge template subscription
     */
    public void setServiceChargeTemplateSubscription(List<ServiceChargeTemplateSubscriptionDto> serviceChargeTemplateSubscription) {
        this.serviceChargeTemplateSubscription = serviceChargeTemplateSubscription;
    }

    @Override
    public String toString() {
        return "ServiceChargeTemplateSubscriptionsDto [serviceChargeTemplateSubscription=" + serviceChargeTemplateSubscription + "]";
    }

}