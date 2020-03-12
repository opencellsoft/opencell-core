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