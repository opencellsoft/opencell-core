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

package org.meveo.model.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains CreateMinAmounts result
 * 
 * @author abdelmounaim akadid
 * @since 7.4.0
 */
public class CreateMinAmountsResult implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3290648156748434424L;
    
    /**
     * A map of amounts created with subscription id as a main key and a secondary map of "&lt;seller.id&gt;_&lt;invoiceSubCategory.id&gt; as a key a and amounts as values"
    as a value 
    */
    Map<Long, Map<String, Amounts>> createdAmountServices;
    
    /**
     * Additional Rated transaction amounts created to reach minimum invoicing amount per subscription. A map of &lt;seller.id&gt;_&lt;invoiceSubCategory.id&gt; as a key a
     and amounts as values 
    */
    Map<String, Amounts> createdAmountSubscription;
    
    
    List<RatedTransaction> minAmountTransactions = new ArrayList<RatedTransaction>();

    /**
     * Instantiate
     */
    public CreateMinAmountsResult() {
    }

    /**
     * @return the createdAmountServices
     */
    public Map<Long, Map<String, Amounts>> getCreatedAmountServices() {
        return createdAmountServices;
    }

    /**
     * @param createdAmountServices the createdAmountServices to set
     */
    public void setCreatedAmountServices(Map<Long, Map<String, Amounts>> createdAmountServices) {
        this.createdAmountServices = createdAmountServices;
    }

    /**
     * @return the minAmountTransactions
     */
    public List<RatedTransaction> getMinAmountTransactions() {
        return minAmountTransactions;
    }

    /**
     * @param minAmountTransactions the minAmountTransactions to set
     */
    public void setMinAmountTransactions(List<RatedTransaction> minAmountTransactions) {
        this.minAmountTransactions = minAmountTransactions;
    }
    
    /**
     * Add a ratedTransaction
     * 
     * @param ratedTransaction Amount without tax
     */
    public void addMinAmountRT(RatedTransaction ratedTransaction) {
        minAmountTransactions.add(ratedTransaction);
    }

    /**
     * @return the createdAmountSubscription
     */
    public Map<String, Amounts> getCreatedAmountSubscription() {
        return createdAmountSubscription;
    }

    /**
     * @param createdAmountSubscription the createdAmountSubscription to set
     */
    public void setCreatedAmountSubscription(Map<String, Amounts> createdAmountSubscription) {
        this.createdAmountSubscription = createdAmountSubscription;
    }
    
}