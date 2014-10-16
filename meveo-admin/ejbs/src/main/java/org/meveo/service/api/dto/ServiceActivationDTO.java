/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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