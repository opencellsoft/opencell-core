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
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;

public class CheckedSubscription {
	private OfferTemplate offerTemplate;
	private UserAccount userAccount;
	private Subscription subscription;
	private Seller seller;
	private List<org.meveo.model.jaxb.subscription.ServiceInstance> serviceInsts = new ArrayList<org.meveo.model.jaxb.subscription.ServiceInstance>();
	private List<org.meveo.model.jaxb.subscription.Access> accessPoints = new ArrayList<org.meveo.model.jaxb.subscription.Access>();

	/**
	 * @return offerTemplate
	 */
	public OfferTemplate getOfferTemplate() {
		return offerTemplate;
	}

	/**
	 * set a new value for offerTemplate
	 */
	public void setOfferTemplate(OfferTemplate value) {
		this.offerTemplate = value;
	}

	/**
	 * @return serviceInsts
	 */
	public List<org.meveo.model.jaxb.subscription.ServiceInstance> getServiceInstances() {
		return serviceInsts;
	}

	/**
	 * set a new value for serviceInsts
	 */
	public void setServiceInstances(List<org.meveo.model.jaxb.subscription.ServiceInstance> serviceInstances) {
		this.serviceInsts = serviceInstances;
	}

	/**
	 * @return accessPoints
	 */
	public List<org.meveo.model.jaxb.subscription.Access> getAccessPoints() {
		return accessPoints;
	}

	/**
	 * set a new value for accessPoints
	 */
	public void setAccessPoints(List<org.meveo.model.jaxb.subscription.Access> accessPoints) {
		this.accessPoints = accessPoints;
	}

	/**
	 * @return seller
	 */
	public Seller getSeller() {
		return seller;
	}

	/**
	 * set a new value for seller
	 */
	public void setSeller(Seller value) {
		this.seller = value;
	}

	/**
	 * @return userAccount
	 */
	public UserAccount getUserAccount() {
		return userAccount;
	}

	/**
	 * set a new value for userAccount
	 */
	public void setUserAccount(UserAccount value) {
		this.userAccount = value;
	}

	/**
	 * @return subscription
	 */
	public Subscription getSubscription() {
		return subscription;
	}

	/**
	 * set a new value for subscription
	 */
	public void setSubscription(Subscription value) {
		this.subscription = value;
	}
}
