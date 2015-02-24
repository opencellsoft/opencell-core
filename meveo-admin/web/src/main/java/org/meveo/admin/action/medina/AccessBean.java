/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.medina;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldEnabledBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.medina.impl.CDRParsingService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link Access} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
@CustomFieldEnabledBean(accountLevel=AccountLevelEnum.ACC)
public class AccessBean extends BaseBean<Access> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link PriceCode} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private AccessService accessService;

	@Inject
	private SubscriptionService subscriptionService;

	@EJB
	private CDRParsingService cdrParsingService;

	@Inject
	@RequestParam
	private Instance<Long> subscriptionId;

	private Subscription selectedSubscription;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public AccessBean() {
		super(Access.class);
	}

	@Override
	public Access initEntity() {
		super.initEntity();

		log.debug("AccesBean initEntity id={}", entity.getId());
		if (subscriptionId.get() != null) {
			Subscription subscription = subscriptionService.findById(subscriptionId.get());
			entity.setStartDate(subscription.getSubscriptionDate());
			entity.setSubscription(subscription);
		}

		return entity;
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

	public Subscription getSelectedSubscription() {
		return selectedSubscription;
	}

	public void setSelectedSubscription(Subscription selectedSubscription) {
		this.selectedSubscription = selectedSubscription;
	}

	public String saveOrUpdate() throws BusinessException {
		if (subscriptionId.get() != null) {
			Subscription subscription = subscriptionService.findById(subscriptionId.get());
			entity.setSubscription(subscription);
		}

		saveOrUpdate(false);

		return "";
	}

	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		String result = "";
		Subscription subscription = subscriptionService.findById(entity.getSubscription().getId());
		entity.setSubscription(subscription);

		if (entity.isTransient()) {
			if (accessService.isDuplicate(entity)) {
				messages.error(new BundleKey("messages", "access.duplicate"));
				return result;
			}
		}

		return super.saveOrUpdate(killConversation);
	}

	public void resetEntity() {
		entity = new Access();

		if (subscriptionId.get() != null) {
			Subscription subscription = subscriptionService.findById(subscriptionId.get());
			entity.setStartDate(subscription.getSubscriptionDate());
			entity.setSubscription(subscription);
		}

		initCustomFields();
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}
}