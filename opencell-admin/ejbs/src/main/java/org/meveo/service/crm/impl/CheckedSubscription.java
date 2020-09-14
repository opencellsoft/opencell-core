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

package org.meveo.service.crm.impl;

import java.util.ArrayList;
import java.util.List;

import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.jaxb.subscription.Access;
import org.meveo.model.jaxb.subscription.ServiceInstance;

public class CheckedSubscription {
	private OfferTemplate offerTemplate;
	private UserAccount userAccount;
	private Subscription subscription;
	private Seller seller;
	private List<org.meveo.model.jaxb.subscription.ServiceInstance> serviceInsts = new ArrayList<org.meveo.model.jaxb.subscription.ServiceInstance>();
	private List<org.meveo.model.jaxb.subscription.Access> accessPoints = new ArrayList<org.meveo.model.jaxb.subscription.Access>();

	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(OfferTemplate offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public List<ServiceInstance> getServiceInsts() {
		return serviceInsts;
	}

	public void setServiceInsts(List<ServiceInstance> serviceInsts) {
		this.serviceInsts = serviceInsts;
	}

	public List<Access> getAccessPoints() {
		return accessPoints;
	}

	public void setAccessPoints(List<Access> accessPoints) {
		this.accessPoints = accessPoints;
	}
}
