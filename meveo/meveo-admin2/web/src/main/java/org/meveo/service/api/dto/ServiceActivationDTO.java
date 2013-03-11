/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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