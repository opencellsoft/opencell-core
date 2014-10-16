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
 * Contains information about a subscription
 * 
 * @author Andrius Karpavicius
 */
public class SubscriptionDTO implements Serializable {

    private static final long serialVersionUID = 3509970630183885055L;

    private String userAccountCode;
    private String code;
    private String description;
    private String offerCode;
    private Date subscriptionDate;
    private Date terminationDate;

    public SubscriptionDTO(String userAccountCode, String code, String description, String offerCode, Date subscriptionDate, Date terminationDate) {
        this.userAccountCode = userAccountCode;
        this.code = code;
        this.description = description;
        this.offerCode = offerCode;
        this.subscriptionDate = subscriptionDate;
        this.terminationDate = terminationDate;
    }

    public String getUserAccountCode() {
        return userAccountCode;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }
}