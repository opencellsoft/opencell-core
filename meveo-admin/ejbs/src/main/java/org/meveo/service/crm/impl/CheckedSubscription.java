package org.meveo.service.crm.impl;

import java.util.ArrayList;
import java.util.List;

import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;

public class CheckedSubscription {
		 public OfferTemplate offerTemplate;
		 public UserAccount userAccount;
		 public Subscription subscription;
		 public List<org.meveo.model.jaxb.subscription.ServiceInstance> serviceInsts = new ArrayList<org.meveo.model.jaxb.subscription.ServiceInstance>();
}
