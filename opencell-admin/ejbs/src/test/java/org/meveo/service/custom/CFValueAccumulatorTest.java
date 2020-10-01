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

package org.meveo.service.custom;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
public class CFValueAccumulatorTest {

    @Test
    public void testCFAccumulation() {

        List<CustomFieldTemplate> cfts = new ArrayList<>();

        String[] appliesTos = new String[] { "Provider", "Seller", "Customer", "BillingAccount", "Subscription", "OfferTemplate" };

        for (String appliesTo : appliesTos) {
            CustomFieldTemplate cft = new CustomFieldTemplate();
            cft.setCode("one");
            cft.setAppliesTo(appliesTo);
            cfts.add(cft);
        }

        CfValueAccumulatorRule rule = new CfValueAccumulatorRule(cfts);

        Assert.assertTrue(rule.getPropagateTo().containsKey(Provider.class));
        Assert.assertTrue(rule.getPropagateTo().containsKey(Seller.class));
        Assert.assertTrue(rule.getPropagateTo().containsKey(Customer.class));
        Assert.assertTrue(rule.getPropagateTo().containsKey(BillingAccount.class));
        Assert.assertTrue(rule.getPropagateTo().containsKey(OfferTemplate.class));
        Assert.assertEquals(rule.getPropagateTo().size(), 5);
        Assert.assertArrayEquals(new Class[] { Seller.class }, rule.getPropagateTo().get(Provider.class).toArray());
        Assertions.assertThat(rule.getPropagateTo().get(Seller.class).size()).isEqualTo(2);
        Assertions.assertThat(rule.getPropagateTo().get(Seller.class).toArray()).contains(Seller.class);
        Assertions.assertThat(rule.getPropagateTo().get(Seller.class).toArray()).contains(Customer.class);
        Assert.assertArrayEquals(new Class[] { BillingAccount.class }, rule.getPropagateTo().get(Customer.class).toArray());
        Assert.assertArrayEquals(new Class[] { Subscription.class }, rule.getPropagateTo().get(BillingAccount.class).toArray());
        Assert.assertArrayEquals(new Class[] { Subscription.class }, rule.getPropagateTo().get(OfferTemplate.class).toArray());

        Assert.assertTrue(rule.getAcumulateFrom().containsKey(Seller.class));
        Assert.assertTrue(rule.getAcumulateFrom().containsKey(Customer.class));
        Assert.assertTrue(rule.getAcumulateFrom().containsKey(BillingAccount.class));
        Assert.assertTrue(rule.getAcumulateFrom().containsKey(Subscription.class));
        Assert.assertEquals(rule.getAcumulateFrom().size(), 4);
        Assert.assertArrayEquals(new CfValueAccumulatorPath[] { new CfValueAccumulatorPath(Provider.class, null), new CfValueAccumulatorPath(Seller.class, "seller") },
            rule.getAcumulateFrom().get(Seller.class).toArray());
        Assert.assertArrayEquals(new CfValueAccumulatorPath[] { new CfValueAccumulatorPath(Seller.class, "seller") }, rule.getAcumulateFrom().get(Customer.class).toArray());
        Assert.assertArrayEquals(new CfValueAccumulatorPath[] { new CfValueAccumulatorPath(Customer.class, "customerAccount.customer") },
            rule.getAcumulateFrom().get(BillingAccount.class).toArray());
        Assert.assertArrayEquals(new CfValueAccumulatorPath[] { new CfValueAccumulatorPath(BillingAccount.class, "userAccount.billingAccount"),
                new CfValueAccumulatorPath(OfferTemplate.class, "offer") },
            rule.getAcumulateFrom().get(Subscription.class).toArray());
    }
}