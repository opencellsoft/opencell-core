package org.meveo.service.custom;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;

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
        Assert.assertArrayEquals(new Class[] { Customer.class, Seller.class }, rule.getPropagateTo().get(Seller.class).toArray());
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