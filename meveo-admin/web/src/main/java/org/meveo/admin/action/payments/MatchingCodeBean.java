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
package org.meveo.admin.action.payments;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.MatchingCode;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link MatchingCode} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class MatchingCodeBean extends BaseBean<MatchingCode> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link MatchingCode} service. Extends {@link PersistenceService}
	 */
	@Inject
	private MatchingCodeService matchingCodeService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public MatchingCodeBean() {
		super(MatchingCode.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<MatchingCode> getPersistenceService() {
		return matchingCodeService;
	}

	public String backToCA() {
		String returnPage = null;

		returnPage = "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId="
				+ entity.getMatchingAmounts().get(0).getAccountOperation()
						.getCustomerAccount().getId()
				+ "&edit=false&tab=ops&faces-redirect=true";

		return returnPage;
	}

	public String unmatching() {
		String returnPage = null;
		try {
			returnPage = "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId="
					+ entity.getMatchingAmounts().get(0).getAccountOperation()
							.getCustomerAccount().getId()
					+ "&edit=false&tab=ops";
			matchingCodeService.unmatching(entity.getId(), getCurrentUser());
			messages.info(new BundleKey("messages", "matchingCode.unmatchingOK"));
		} catch (BusinessException e) {
			messages.error(new BundleKey("messages",
					"matchingCode.unmatchingKO"));
			log.error(e.getMessage());
		}
		return returnPage;
	}
}