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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.order.OrderItemActionEnum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The Class UpdateServicesRequestDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "UpdateServicesRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties({ "orderNumber", "orderItemId", "orderItemAction" })
public class UpdateServicesRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8352154466061113933L;

    /** The subscription code. */
    @XmlElement(required = true)
    private String subscriptionCode;

    /** The subscription validity date. */
    private Date subscriptionValidityDate;

    /** The services to update. */
    @XmlElement(name = "serviceToUpdate")
    @XmlElementWrapper(name = "servicesToUpdate")
    private List<ServiceToUpdateDto> servicesToUpdate;

    /** The order number. */
    private String orderNumber;

    /** The order item id. */
    private Long orderItemId;

    /** The order item action. */
    private OrderItemActionEnum orderItemAction;

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
     * Gets the services to update.
     *
     * @return the services to update
     */
    public List<ServiceToUpdateDto> getServicesToUpdate() {
        return servicesToUpdate;
    }

    /**
     * Sets the services to update.
     *
     * @param servicesToUpdate the new services to update
     */
    public void setServicesToUpdate(List<ServiceToUpdateDto> servicesToUpdate) {
        this.servicesToUpdate = servicesToUpdate;
    }

    /**
     * Adds the service.
     *
     * @param serviceToUpdate the service to update
     */
    public void addService(ServiceToUpdateDto serviceToUpdate) {
        if (servicesToUpdate == null) {
            servicesToUpdate = new ArrayList<>();
        }
        servicesToUpdate.add(serviceToUpdate);
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
     * Sets the order item action.
     *
     * @param action the new order item action
     */
    public void setOrderItemAction(OrderItemActionEnum action) {
        this.orderItemAction = action;
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

    public Date getSubscriptionValidityDate() {
        return subscriptionValidityDate;
    }

    public void setSubscriptionValidityDate(Date subscriptionValidityDate) {
        this.subscriptionValidityDate = subscriptionValidityDate;
    }
}