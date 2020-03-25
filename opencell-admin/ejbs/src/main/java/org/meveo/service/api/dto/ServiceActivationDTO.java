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
package org.meveo.service.api.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Contains information for service activation request
 * 
 * @author Andrius Karpavicius
 */
public class ServiceActivationDTO implements Serializable {

    private static final long serialVersionUID = 8571795417765965626L;

    private String subscriptionCode;

    private String serviceCode;

    private Date activationDate;

    private int quantity = 1;

    public ServiceActivationDTO(String subscriptionCode, String serviceCode, Date activationDate, int quantity) {
        this.subscriptionCode = subscriptionCode;
        this.serviceCode = serviceCode;
        this.activationDate = activationDate;
        this.quantity = quantity;
    }

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public int getQuantity() {
        return quantity;
    }
}