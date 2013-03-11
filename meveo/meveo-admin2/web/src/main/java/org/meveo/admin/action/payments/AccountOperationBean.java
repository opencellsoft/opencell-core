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

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.PartialMatchingOccToSelect;
import org.meveo.model.admin.User;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.local.AccountOperationServiceLocal;
import org.meveo.service.payments.local.MatchingCodeServiceLocal;

/**
 * Standard backing bean for {@link AccountOperation} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Ignas
 * @created 2009.10.13
 */
@Name("accountOperationBean")
@Scope(ScopeType.CONVERSATION)
public class AccountOperationBean extends BaseBean<AccountOperation> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link AccountOperation} service. Extends
	 * {@link PersistenceService}.
	 */
	@In
	private AccountOperationServiceLocal accountOperationService;
	@In
	private User currentUser;

	@In
	private MatchingCodeServiceLocal matchingCodeService;

	@SuppressWarnings("unused")
	@Out(required = false)
	private AutomatedPayment automatedPayment;

	@SuppressWarnings("unused")
	@Out(required = false)
	private RecordedInvoice recordedInvoice;

	@Out(required = false)
	private List<PartialMatchingOccToSelect> partialMatchingOps = new ArrayList<PartialMatchingOccToSelect>();

	@Out(required = false)
	private List<MatchingAmount> matchingAmounts = new ArrayList<MatchingAmount>();

	public List<PartialMatchingOccToSelect> getPartialMatchingOps() {
		return partialMatchingOps;
	}

	public void setPartialMatchingOps(List<PartialMatchingOccToSelect> partialMatchingOps) {
		this.partialMatchingOps = partialMatchingOps;
	}

	/**
	 * TODO
	 */
	@SuppressWarnings("unused")
	@Out(required = false)
	private OtherCreditAndCharge otherCreditAndCharge;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public AccountOperationBean() {
		super(AccountOperation.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Factory("accountOperation")
	@Begin(nested = true)
	public AccountOperation init() {
		return initEntity();
	}

	/**
	 * Data model of entities for data table in GUI.
	 * 
	 * @return filtered entities.
	 */
	@Out(value = "accountOperations", required = false)
	protected PaginationDataModel<AccountOperation> getDataModel() {
		// final FacesContext context = FacesContext.getCurrentInstance();
		// context.getExternalContext().getRequestParameterMap().put("sortField",
		// "transactionDate");
		// context.getExternalContext().getRequestParameterMap().put("sortOrder",
		// "desc");
		super.list();
		return entities;
	}

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	@Override
	@Begin(join = true)
	@Factory("accountOperations")
	public void list() {
		super.list();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	@End(beforeRedirect = true, root=true)
	public String saveOrUpdate() {
		return saveOrUpdate(entity);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<AccountOperation> getPersistenceService() {
		return accountOperationService;
	}

	/**
	 * TODO
	 */
	public String displayOperation(Long accountOperationId) {
		String page = "/pages/payments/accountOperations/showOcc.xhtml";
		AccountOperation accountOperation = accountOperationService.findById(accountOperationId);
		if (accountOperation instanceof RecordedInvoice) {
			page = "/pages/payments/accountOperations/showInvoice.xhtml";
		}
		if (accountOperation instanceof AutomatedPayment) {
			automatedPayment = (AutomatedPayment) accountOperation;
			page = "/pages/payments/accountOperations/showAutomatedPayment.xhtml";
		}
		return page;
	}

	/**
	 * match selected operations
	 * 
	 * @return
	 */
	public String matching(Long customerAccountId) {
		List<Long> operationIds = new ArrayList<Long>();
		log.debug("getChecked():" + getChecked());
		for (Long id : getChecked().keySet()) {
			if (getChecked().get(id)) {
				operationIds.add(id);
			}
		}
		log.info("operationIds    " + operationIds);
		if (operationIds.isEmpty()) {
			statusMessages.addFromResourceBundle(Severity.ERROR, "customerAccount.matchingUnselectedOperation");
			return null;
		}
		try {
			MatchingReturnObject result = matchingCodeService.matchOperations(customerAccountId, null, operationIds, null, currentUser);
			if (result.isOk()) {
				statusMessages.addFromResourceBundle("customerAccount.matchingSuccessful");
			} else {
				setPartialMatchingOps(result.getPartialMatchingOcc());
				return "/pages/payments/customerAccounts/partialMatching.seam?objectId=" + customerAccountId + "";
			}

		} catch (NoAllOperationUnmatchedException ee) {
			statusMessages.addFromResourceBundle(Severity.ERROR, "customerAccount.noAllOperationUnmatched");
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.addFromResourceBundle(Severity.ERROR, e.getMessage());
		}
		return "/pages/payments/customerAccounts/customerAccountDetail.seam?objectId=" + customerAccountId + "&edit=false&tab=ops";
	}

	// called from page of selection partial operation
	public String partialMatching(PartialMatchingOccToSelect partialMatchingOccSelected) {
		List<Long> operationIds = new ArrayList<Long>();
		for (PartialMatchingOccToSelect p : getPartialMatchingOps()) {
			operationIds.add(p.getAccountOperation().getId());
		}
		try {
			MatchingReturnObject result = matchingCodeService.matchOperations(partialMatchingOccSelected.getAccountOperation().getCustomerAccount().getId(),
					null,
					operationIds, partialMatchingOccSelected.getAccountOperation().getId(), currentUser);
			if (result.isOk()) {
				statusMessages.addFromResourceBundle("customerAccount.matchingSuccessful");
			} else {
				statusMessages.addFromResourceBundle(Severity.ERROR, "customerAccount.matchingFailed");
			}
		} catch (NoAllOperationUnmatchedException ee) {
			statusMessages.addFromResourceBundle(Severity.ERROR, "customerAccount.noAllOperationUnmatched");
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
		}
		return "/pages/payments/customerAccounts/customerAccountDetail.seam?objectId="
				+ partialMatchingOccSelected.getAccountOperation().getCustomerAccount().getId() + "&edit=false&tab=ops";
	}

	/**
	 * Consult Matching code page
	 * 
	 * @return the URL of the matching code page containing the selected
	 *         operation
	 */

	public String consultMatching() {
		List<Long> operationIds = new ArrayList<Long>();
		for (Long id : getChecked().keySet()) {
			if (getChecked().get(id)) {
				operationIds.add(id);
			}
		}
		log.info(" consultMatching operationIds " + operationIds);
		if (operationIds.isEmpty() || operationIds.size() > 1) {
			statusMessages.addFromResourceBundle("consultMatching.noOperationSelected");
			return null;
		}
		AccountOperation accountOperation = accountOperationService.findById(operationIds.get(0));
		if (accountOperation.getMatchingStatus() != MatchingStatusEnum.L && accountOperation.getMatchingStatus() != MatchingStatusEnum.P) {
			statusMessages.addFromResourceBundle("consultMatching.operationNotMatched");
			return null;
		}
		matchingAmounts = accountOperation.getMatchingAmounts();
		if (matchingAmounts.size() == 1) {
			return "/pages/payments/matchingCode/matchingCodeDetail.seam?objectId=" + matchingAmounts.get(0).getMatchingCode().getId() + "&edit=false";
		}
		return "/pages/payments/matchingCode/selectMatchingCode.seam?objectId=" + accountOperation.getId() + "&edit=false";
	}

	public List<MatchingAmount> getMatchingAmounts() {
		return matchingAmounts;
	}

	public void setMatchingAmounts(List<MatchingAmount> matchingAmounts) {
		this.matchingAmounts = matchingAmounts;
	}

}