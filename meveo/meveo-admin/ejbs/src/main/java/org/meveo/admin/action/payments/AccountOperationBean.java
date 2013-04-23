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
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.model.IEntity;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.PartialMatchingOccToSelect;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.MatchingCodeService;

/**
 * Standard backing bean for {@link AccountOperation} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Ignas
 * @created 2009.10.13
 */
@Named
@ConversationScoped
public class AccountOperationBean extends BaseBean<AccountOperation> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link AccountOperation} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private AccountOperationService accountOperationService;

	@Inject
	private MatchingCodeService matchingCodeService;

	@SuppressWarnings("unused")
	// TODO: @Out(required = false)
	private AutomatedPayment automatedPayment;

	@SuppressWarnings("unused")
	// TODO: @Out(required = false)
	private RecordedInvoice recordedInvoice;

	// TODO: @Out(required = false)
	private List<PartialMatchingOccToSelect> partialMatchingOps = new ArrayList<PartialMatchingOccToSelect>();

	// TODO: @Out(required = false)
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
	// TODO: @Out(required = false)
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
	@Produces
	@Named("accountOperation")
	public AccountOperation init() {
		return initEntity();
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
		log.debug("getChecked():" + getSelectedEntities());
		for (IEntity operation : getSelectedEntities()) {
		    operationIds.add((Long)operation.getId());
		}
		log.info("operationIds    " + operationIds);
		if (getSelectedEntities().length==0) {
			messages.error(new BundleKey("messages", "customerAccount.matchingUnselectedOperation"));
			return null;
		}
		try {
			MatchingReturnObject result = matchingCodeService.matchOperations(customerAccountId,
					null, operationIds, null, getCurrentUser());
			if (result.isOk()) {
				messages.info(new BundleKey("messages", "customerAccount.matchingSuccessful"));
			} else {
				setPartialMatchingOps(result.getPartialMatchingOcc());
				return "/pages/payments/customerAccounts/partialMatching.xhtml?objectId="
						+ customerAccountId + "";
			}

		} catch (NoAllOperationUnmatchedException ee) {
			messages.error(new BundleKey("messages", "customerAccount.noAllOperationUnmatched"));
		} catch (Exception e) {
			e.printStackTrace();
			messages.error(e.getMessage());
		}
		return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId="
				+ customerAccountId + "&edit=false&tab=ops";
	}

	// called from page of selection partial operation
	public String partialMatching(PartialMatchingOccToSelect partialMatchingOccSelected) {
		List<Long> operationIds = new ArrayList<Long>();
		for (PartialMatchingOccToSelect p : getPartialMatchingOps()) {
			operationIds.add(p.getAccountOperation().getId());
		}
		try {
			MatchingReturnObject result = matchingCodeService.matchOperations(
					partialMatchingOccSelected.getAccountOperation().getCustomerAccount().getId(),
					null, operationIds, partialMatchingOccSelected.getAccountOperation().getId(),
					getCurrentUser());
			if (result.isOk()) {
				messages.info(new BundleKey("messages", "customerAccount.matchingSuccessful"));
			} else {
				messages.error(new BundleKey("messages", "customerAccount.matchingFailed"));
			}
		} catch (NoAllOperationUnmatchedException ee) {
			messages.error(new BundleKey("messages", "customerAccount.noAllOperationUnmatched"));
		} catch (Exception e) {
			e.printStackTrace();
			messages.error(e.getMessage());
		}
		return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId="
				+ partialMatchingOccSelected.getAccountOperation().getCustomerAccount().getId()
				+ "&edit=false&tab=ops";
	}

	/**
	 * Consult Matching code page
	 * 
	 * @return the URL of the matching code page containing the selected
	 *         operation
	 */

	public String consultMatching() {
		List<Long> operationIds = new ArrayList<Long>();
		log.debug("getChecked():" + getSelectedEntities());
        for (IEntity operation : getSelectedEntities()) {
            operationIds.add((Long)operation.getId());
        }
		log.info(" consultMatching operationIds " + operationIds);
		if (operationIds.isEmpty() || operationIds.size() > 1) {
			messages.info(new BundleKey("messages", "consultMatching.noOperationSelected"));
			return null;
		}
		AccountOperation accountOperation = accountOperationService.findById(operationIds.get(0));
		if (accountOperation.getMatchingStatus() != MatchingStatusEnum.L
				&& accountOperation.getMatchingStatus() != MatchingStatusEnum.P) {
			messages.info(new BundleKey("messages", "consultMatching.operationNotMatched"));
			return null;
		}
		matchingAmounts = accountOperation.getMatchingAmounts();
		if (matchingAmounts.size() == 1) {
			return "/pages/payments/matchingCode/matchingCodeDetail.xhtml?objectId="
					+ matchingAmounts.get(0).getMatchingCode().getId() + "&edit=false";
		}
		return "/pages/payments/matchingCode/selectMatchingCode.xhtml?objectId="
				+ accountOperation.getId() + "&edit=false";
	}

	public List<MatchingAmount> getMatchingAmounts() {
		return matchingAmounts;
	}

	public void setMatchingAmounts(List<MatchingAmount> matchingAmounts) {
		this.matchingAmounts = matchingAmounts;
	}

	public String getDate(){
	    return (new Date()).toString();
	}
}