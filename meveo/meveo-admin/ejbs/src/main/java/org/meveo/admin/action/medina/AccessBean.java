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
package org.meveo.admin.action.medina;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.billing.SubscriptionBean;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.medina.impl.AccessService;
import org.primefaces.event.SelectEvent;

@Named
@ConversationScoped
public class AccessBean extends BaseBean<Access> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link PriceCode} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private AccessService accessService;

	@Inject
	private SubscriptionBean subscriptionBean;

	private Subscription selectedSubscription;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public AccessBean() {
		super(Access.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Access> getPersistenceService() {
		return accessService;
	}

	@Override
	protected String getListViewName() {
		return "access";
	}

	@Override
	public String getEditViewName() {
		return "accessDetail";
	}

	public Subscription getSelectedSubscription() {
		return selectedSubscription;
	}

	public void setSelectedSubscription(Subscription selectedSubscription) {
		this.selectedSubscription = selectedSubscription;
	}
}
