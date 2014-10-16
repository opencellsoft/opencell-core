/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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
package org.meveo.admin.action.billing;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link UserAccount} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 */
@Named
@ConversationScoped
public class UserAccountBean extends BaseBean<UserAccount> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected
	 * 
	 * @{link UserAccount} service. Extends {@link PersistenceService} .
	 */

	@Inject
	private WalletOperationBean walletOperationBean;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	private Long billingAccountId;

	@Inject
	private BillingAccountService billingAccountService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public UserAccountBean() {
		super(UserAccount.class);
	}

	public Long getBillingAccountId() {
		return billingAccountId;
	}

	public void setBillingAccountId(Long billingAccountId) {
		this.billingAccountId = billingAccountId;
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public UserAccount initEntity() {
		super.initEntity();
		System.out.println("initentity useraccount id=" + entity.getId());
		if (entity.getId() == null && billingAccountId != null) {
			BillingAccount billingAccount = billingAccountService
					.findById(billingAccountId);
			entity.setBillingAccount(billingAccount);
			populateAccounts(billingAccount);

			// check if has default
			if (!billingAccount.getDefaultLevel()) {
				entity.setDefaultLevel(true);
			}
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation) {
		try {
			if (entity.getDefaultLevel()) {
				if (userAccountService.isDuplicationExist(entity)) {
					entity.setDefaultLevel(false);
					throw new DuplicateDefaultAccountException();
				}

			}
			super.saveOrUpdate(killConversation);
			return "/pages/billing/userAccounts/userAccountDetail.xhtml?edit=false&userAccountId="
					+ entity.getId()
					+ "&faces-redirect=true&includeViewParams=true";
		} catch (DuplicateDefaultAccountException e1) {
			messages.error(new BundleKey("messages",
					"error.account.duplicateDefautlLevel"));
		} catch (Exception e) {
			e.printStackTrace();
			messages.error(new BundleKey("messages", "javax.el.ELException"));

		}

		return null;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<UserAccount> getPersistenceService() {
		return userAccountService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	@Override
	protected String saveOrUpdate(UserAccount entity) {
		try {
			if (entity.isTransient()) {
				userAccountService.createUserAccount(
						entity.getBillingAccount(), entity, getCurrentUser());
				messages.info(new BundleKey("messages", "save.successful"));
			} else {
				userAccountService.updateUserAccount(entity, getCurrentUser());
				messages.info(new BundleKey("messages", "update.successful"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			messages.error(e.getMessage());
		}

		return back();
	}

	public void terminateAccount() {
		log.debug("resiliateAccount userAccountId:" + entity.getId());
		try {
			userAccountService.userAccountTermination(entity,
					entity.getTerminationDate(), entity.getTerminationReason(),
					getCurrentUser());
			messages.info(new BundleKey("messages",
					"resiliation.resiliateSuccessful"));
		} catch (BusinessException e) {
			e.printStackTrace();
			messages.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			messages.error(e.getMessage());
		}
	}

	public String cancelAccount() {
		log.info("cancelAccount userAccountId:" + entity.getId());
		try {
			userAccountService.userAccountCancellation(entity, new Date(),
					getCurrentUser());
			messages.info(new BundleKey("messages",
					"cancellation.cancelSuccessful"));
			return "/pages/billing/userAccounts/userAccountDetail.xhtml?objectId="
					+ entity.getId() + "&edit=false";
		} catch (BusinessException e) {
			e.printStackTrace();
			messages.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			messages.error(e.getMessage());
		}
		return null;
	}

	public String reactivateAccount() {
		log.info("reactivateAccount userAccountId:" + entity.getId());
		try {
			userAccountService.userAccountReactivation(entity, new Date(),
					getCurrentUser());
			messages.info(new BundleKey("messages",
					"reactivation.reactivateSuccessful"));
			return "/pages/billing/userAccounts/userAccountDetail.xhtml?objectId="
					+ entity.getId() + "&edit=false";
		} catch (BusinessException e) {
			e.printStackTrace(); // TODO WTF printStackTrace??
			messages.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			messages.error(e.getMessage());
		}
		return null;
	}

	public LazyDataModel<WalletOperation> getWalletOperationsNoInvoiced() {
		System.out.println("getWalletOperationsNoInvoiced");
		LazyDataModel<WalletOperation> result = null;
		HashMap<String, Object> filters = new HashMap<String, Object>();
		if (entity == null) {
			System.out
					.println("getWalletOperationsNoInvoiced: userAccount is null");
			initEntity();
			System.out.println("entity.id=" + entity.getId());
		}
		if (entity.getWallet() == null) {
			System.out.println("getWalletOperationsNoInvoiced: userAccount "
					+ entity.getId() + " has no wallet");
		} else {
			filters.put("wallet", entity.getWallet());
			filters.put("status", WalletOperationStatusEnum.OPEN);
			result = walletOperationBean.getLazyDataModel(filters, true);
		}
		return result;
	}

	@Produces
	@Named("getRatedTransactionsInvoiced")
	public List<RatedTransaction> getRatedTransactionsInvoiced() {
		return ratedTransactionService.getRatedTransactionsInvoiced(entity);
	}

	public void populateAccounts(BillingAccount billingAccount) {

		entity.setBillingAccount(billingAccount);
		if (userAccountService.isDuplicationExist(entity)) {
			entity.setDefaultLevel(false);
		} else {
			entity.setDefaultLevel(true);
		}
		if (billingAccount.getProvider() != null
				&& billingAccount.getProvider().isLevelDuplication()) {
			entity.setCode(billingAccount.getCode());
			entity.setDescription(billingAccount.getDescription());
			entity.setAddress(billingAccount.getAddress());
			entity.setExternalRef1(billingAccount.getExternalRef1());
			entity.setExternalRef2(billingAccount.getExternalRef2());
			entity.setProviderContact(billingAccount.getProviderContact());
			entity.setName(billingAccount.getName());
			entity.setProvider(billingAccount.getProvider());
			entity.setSubscriptionDate(billingAccount.getSubscriptionDate());
			entity.setPrimaryContact(billingAccount.getPrimaryContact());
		}
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	
}
