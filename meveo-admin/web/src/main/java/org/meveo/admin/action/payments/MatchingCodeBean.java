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
package org.meveo.admin.action.payments;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.MatchingCode;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.MatchingCodeService;

/**
 * Standard backing bean for {@link MatchingCode} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Tyshan(tyshanchn@manaty.net)
 * @created 2010-12-1
 */
@Named
// TODO: @Scope(ScopeType.PAGE)
@ConversationScoped
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
			e.printStackTrace();
		}
		return returnPage;
	}
}