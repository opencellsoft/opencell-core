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

public class SubscriptionAccessException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4233855493891428488L;
	org.meveo.model.jaxb.subscription.Subscription subscrip;
	org.meveo.model.jaxb.subscription.Access access;
	String mess;
	public SubscriptionAccessException(org.meveo.model.jaxb.subscription.Subscription subscrip,org.meveo.model.jaxb.subscription.Access access,
						String mess){
		this.mess=mess;
		this.access=access;
		this.subscrip=subscrip;
	}
	public org.meveo.model.jaxb.subscription.Subscription getSubscrip() {
		return subscrip;
	}
	public void setSubscrip(org.meveo.model.jaxb.subscription.Subscription subscrip) {
		this.subscrip = subscrip;
	}
	public org.meveo.model.jaxb.subscription.Access getAccess() {
		return access;
	}
	public void setAccess(
			org.meveo.model.jaxb.subscription.Access access) {
		this.access = access;
	}
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	
}
