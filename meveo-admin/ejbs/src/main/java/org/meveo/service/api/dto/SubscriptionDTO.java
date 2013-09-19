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