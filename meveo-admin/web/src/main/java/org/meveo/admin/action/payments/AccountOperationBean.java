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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.model.IEntity;
import org.meveo.model.MatchingReturnObject;
import org.meveo.model.PartialMatchingOccToSelect;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link AccountOperation} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
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

	public void setPartialMatchingOps(
			List<PartialMatchingOccToSelect> partialMatchingOps) {
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
		AccountOperation accountOperation = accountOperationService
				.findById(accountOperationId);
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
			operationIds.add((Long) operation.getId());
		}
		log.info("operationIds    " + operationIds);
		if (operationIds.isEmpty()) {
			messages.error(new BundleKey("messages",
					"customerAccount.matchingUnselectedOperation"));
			return null;
		}
		try {
			MatchingReturnObject result = matchingCodeService.matchOperations(
					customerAccountId, null, operationIds, null,
					getCurrentUser());
			if (result.isOk()) {
				messages.info(new BundleKey("messages",
						"customerAccount.matchingSuccessful"));
			} else {
				setPartialMatchingOps(result.getPartialMatchingOcc());
				return "/pages/payments/customerAccounts/partialMatching.xhtml?objectId="
						+ customerAccountId + "&faces-redirect=true";
			}

		} catch (NoAllOperationUnmatchedException ee) {
			messages.error(new BundleKey("messages",
					"customerAccount.noAllOperationUnmatched"));
		} catch (BusinessException ee) {
			messages.error(new BundleKey("messages", ee.getMessage()));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId="
				+ customerAccountId + "&edit=false&tab=ops&faces-redirect=true";
	}

	// called from page of selection partial operation
	public String partialMatching(
			PartialMatchingOccToSelect partialMatchingOccSelected) {
		List<Long> operationIds = new ArrayList<Long>();
		for (PartialMatchingOccToSelect p : getPartialMatchingOps()) {
			operationIds.add(p.getAccountOperation().getId());
		}
		try {
			MatchingReturnObject result = matchingCodeService.matchOperations(
					partialMatchingOccSelected.getAccountOperation()
							.getCustomerAccount().getId(), null, operationIds,
					partialMatchingOccSelected.getAccountOperation().getId(),
					getCurrentUser());
			if (result.isOk()) {
				messages.info(new BundleKey("messages",
						"customerAccount.matchingSuccessful"));
			} else {
				messages.error(new BundleKey("messages",
						"customerAccount.matchingFailed"));
			}
		} catch (NoAllOperationUnmatchedException ee) {
			messages.error(new BundleKey("messages",
					"customerAccount.noAllOperationUnmatched"));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId="
				+ partialMatchingOccSelected.getAccountOperation()
						.getCustomerAccount().getId()
				+ "&edit=false&tab=ops&faces-redirect=true";
	}

	/**
	 * Consult Matching code page
	 * 
	 * @return the URL of the matching code page containing the selected
	 *         operation
	 */

	private void dunningInclusionExclusionPartial(AccountOperation accountOperation,Boolean exclude){
		for(MatchingAmount  matchingAmount : accountOperation.getMatchingAmounts()){
			   MatchingCode matchingCode = matchingAmount.getMatchingCode();
				   for(MatchingAmount  ma : matchingCode.getMatchingAmounts()){
			           AccountOperation accountop=ma.getAccountOperation();
			           accountop.setExcludedFromDunning(exclude);
			           accountOperationService.update(accountop); }   
			   }
	}
	public String dunningInclusionExclusion(long customerAccountId, boolean exclude) {
		try {
			if (getSelectedEntities() == null
					|| getSelectedEntities().isEmpty()) {
				throw new BusinessEntityException("consultMatching.noOperationSelected");
			} 
			else{
				log.info(" excludedFromDunning operationIds "
						+ getSelectedEntities().size());
				for (IEntity operation : getSelectedEntities()) {
					   AccountOperation accountOperation = (AccountOperation) operation;
					   if(!accountOperation.getExcludedFromDunning()==exclude){
					   if (accountOperation instanceof RecordedInvoice) { 
						 accountOperation.setExcludedFromDunning(exclude);
					    accountOperationService.update(accountOperation);
					      }
					   else {
						throw new BusinessEntityException("excludedFromDunning.selectOperations.notInvoice");
						}
					   if(accountOperation.getMatchingStatus()==MatchingStatusEnum.P){
						     dunningInclusionExclusionPartial(accountOperation,exclude) ;
						     }}
			         }
			}
			messages.info(new BundleKey("messages",
					exclude ? "accountOperation.excludFromDunning"
							: "accountOperation.includFromDunning"));
		} catch (BusinessEntityException e) {
			messages.error(new BundleKey("messages", e.getMessage()));
		}

		return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId="
				+ customerAccountId + "&edit=false&tab=ops&faces-redirect=true";
	}
	
	public boolean isSelectedOperationIncluded(){
		boolean included=true;
		if(getSelectedEntities()!=null){
			for (IEntity operation : getSelectedEntities()) {
		     AccountOperation accountOperation = (AccountOperation) operation;
		     if(accountOperation.getExcludedFromDunning()){
		    	 included=false;
		    	 break;
		    	 }}}
		return included;
	  }
	
	public boolean isSelectedOperationExcluded(){
		boolean excluded=true;
		if(getSelectedEntities()!=null){
			for (IEntity operation : getSelectedEntities()) {
		     AccountOperation accountOperation = (AccountOperation) operation;
		     if(!accountOperation.getExcludedFromDunning()){
		    	 excluded=false;
		    	 break;
		     }}}
		return excluded;
	  }
	

	public String consultMatching(long customerAccountId) {
		List<Long> operationIds = new ArrayList<Long>();
		log.debug("getChecked():" + getSelectedEntities());
		for (IEntity operation : getSelectedEntities()) {
			operationIds.add((Long) operation.getId());
		}
		log.info(" consultMatching operationIds " + operationIds);
		if (operationIds.isEmpty() || operationIds.size() > 1) {
			messages.info(new BundleKey("messages",
					"consultMatching.noOperationSelected"));

			return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId="
					+ customerAccountId
					+ "&edit=false&tab=ops&faces-redirect=true";
		}
		AccountOperation accountOperation = accountOperationService
				.findById(operationIds.get(0));
		if (accountOperation.getMatchingStatus() != MatchingStatusEnum.L
				&& accountOperation.getMatchingStatus() != MatchingStatusEnum.P) {
			messages.info(new BundleKey("messages",
					"consultMatching.operationNotMatched"));

			return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId="
					+ customerAccountId
					+ "&edit=false&tab=ops&faces-redirect=true";
		}
		matchingAmounts = accountOperation.getMatchingAmounts();
		if (matchingAmounts.size() == 1) {
			return "/pages/payments/matchingCode/matchingCodeDetail.xhtml?objectId="
					+ matchingAmounts.get(0).getMatchingCode().getId()
					+ "&edit=false&faces-redirect=true";
		}
		return "/pages/payments/matchingCode/selectMatchingCode.xhtml?objectId="
				+ accountOperation.getId() + "&edit=false&faces-redirect=true";
	}

	public List<MatchingAmount> getMatchingAmounts() {
		return matchingAmounts;
	}

	public void setMatchingAmounts(List<MatchingAmount> matchingAmounts) {
		this.matchingAmounts = matchingAmounts;
	}

	public String getDate() {
		return (new Date()).toString();
	}

	public LazyDataModel<AccountOperation> getAccountOperations(
			CustomerAccount ca) {
		if (!ca.isTransient()) {
			filters.put("customerAccount", ca);
			return getLazyDataModel();
		} else {
			return null;
		}
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "customerAccount");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider", "customerAccount");
	}

}