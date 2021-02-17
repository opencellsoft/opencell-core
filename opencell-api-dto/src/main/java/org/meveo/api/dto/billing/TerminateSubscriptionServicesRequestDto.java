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

package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.order.OrderItemActionEnum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class TerminateSubscriptionServicesRequestDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "TerminateSubscriptionServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({ "orderNumber", "orderItemId", "orderItemAction" })
public class TerminateSubscriptionServicesRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7356243821434866938L;

    /** The services. */
    @XmlElement()
    private List<String> services;

    /** The service ids. */
    @XmlElement()
    private List<Long> serviceIds;

    /** The subscription code. */
    @XmlElement(required = true)
    private String subscriptionCode;

    /** The subscription validity date. */
    private Date subscriptionValidityDate;

    /** The termination reason. */
    @XmlElement(required = true)
    private String terminationReason;

    /** The termination date. */
    @XmlElement(required = true)
    private Date terminationDate;

    /** The order number. */
    private String orderNumber;

    /** The order item id. */
    private Long orderItemId;

    /** The order item action. */
    private OrderItemActionEnum orderItemAction;

    /**
     * Gets the service ids.
     *
     * @return the service ids
     */
    public List<Long> getServiceIds() {
        return serviceIds;
    }

    /**
     * Sets the service ids.
     *
     * @param serviceIds the new service ids
     */
    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }

    /**
     * Adds the service id.
     *
     * @param serviceId the service id
     */
    public void addServiceId(Long serviceId) {
        if (serviceIds == null) {
            serviceIds = new ArrayList<>();
        }
        serviceIds.add(serviceId);
    }

    /**
     * Gets the services.
     *
     * @return the services
     */
    public List<String> getServices() {
        return services;
    }

    /**
     * Adds the service code.
     *
     * @param serviceCode the service code
     */
    public void addServiceCode(String serviceCode) {
        if (services == null) {
            services = new ArrayList<>();
        }
        services.add(serviceCode);
    }

    /**
     * Sets the services.
     *
     * @param services the new services
     */
    public void setServices(List<String> services) {
        this.services = services;
    }

    /**
     * Gets the termination reason.
     *
     * @return the termination reason
     */
    public String getTerminationReason() {
        return terminationReason;
    }

    /**
     * Sets the termination reason.
     *
     * @param terminationReason the new termination reason
     */
    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    /**
     * Gets the termination date.
     *
     * @return the termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the new termination date
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    /**
     * Gets the subscription code.
     *
     * @return the subscription code
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * Sets the subscription code.
     *
     * @param subscriptionCode the new subscription code
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    /**
     * Gets the order number.
     *
     * @return the order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     *
     * @param orderNumber the new order number
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the order item id.
     *
     * @return the order item id
     */
    public Long getOrderItemId() {
        return orderItemId;
    }

    /**
     * Sets the order item id.
     *
     * @param orderItemId the new order item id
     */
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    /**
     * Gets the order item action.
     *
     * @return the order item action
     */
    public OrderItemActionEnum getOrderItemAction() {
        return orderItemAction;
    }

    /**
     * Sets the action.
     *
     * @param action the new action
     */
    public void setAction(OrderItemActionEnum action) {
        this.orderItemAction = action;
    }

    public Date getSubscriptionValidityDate() {
        return subscriptionValidityDate;
    }

    public void setSubscriptionValidityDate(Date subscriptionValidityDate) {
        this.subscriptionValidityDate = subscriptionValidityDate;
    }

    @Override
    public String toString() {
        return "TerminateSubscriptionServicesRequestDto [services=" + services + ", serviceIds=" + serviceIds + ", subscriptionCode=" + subscriptionCode + ", terminationReason="
                + terminationReason + ", terminationDate=" + terminationDate + ", orderNumber=" + orderNumber + "]";
    }
}