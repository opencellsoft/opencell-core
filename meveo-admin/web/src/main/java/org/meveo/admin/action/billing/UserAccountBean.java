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
package org.meveo.admin.action.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.enterprise.inject.Produces;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.AccountBean;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldEnabledBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.cache.WalletCacheContainerProvider;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.OperationTypeEnum;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.WalletReservationService;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Faces;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link UserAccount} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
@CustomFieldEnabledBean(accountLevel=AccountLevelEnum.UA)
public class UserAccountBean extends AccountBean<UserAccount> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected
	 * 
	 * @{link UserAccount} service. Extends {@link PersistenceService} .
	 */

	@Inject
	private WalletOperationBean walletOperationBean;

	@Inject
	WalletOperationService walletOperationService;

	@Inject
	WalletReservationService walletReservationService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Inject
	private BillingAccountService billingAccountService;
	
	@Inject
	private WalletCacheContainerProvider walletCacheContainerProvider;
	   
	private CounterInstance selectedCounterInstance;

	private Long billingAccountId;
	private WalletOperation reloadOperation;
	private String selectedWalletCode;

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

		if (entity.getId() == null && billingAccountId != null) {
			BillingAccount billingAccount = billingAccountService.findById(billingAccountId,
					Arrays.asList("customerAccount"));
			entity.setBillingAccount(billingAccount);
			populateAccounts(billingAccount);

			// check if has default
			if (!billingAccount.getDefaultLevel()) {
				entity.setDefaultLevel(true);
			}
		}
		selectedCounterInstance=entity.getCounters()!=null && entity.getCounters().size()>0?entity.getCounters().values().iterator().next():null;
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

			return "/pages/billing/userAccounts/userAccountDetail.xhtml?edit=false&userAccountId=" + entity.getId()
					+ "&faces-redirect=true&includeViewParams=true";
		} catch (DuplicateDefaultAccountException e1) {
			messages.error(new BundleKey("messages", "error.account.duplicateDefautlLevel"));
		} catch (Exception e) {
			log.error(e.getMessage());
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
				userAccountService.createUserAccount(entity.getBillingAccount(), entity, getCurrentUser());
				messages.info(new BundleKey("messages", "save.successful"));
			} else {
				getPersistenceService().update(entity, getCurrentUser());
				messages.info(new BundleKey("messages", "update.successful"));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}

		return back();
	}

	public void terminateAccount() {
		log.debug("resiliateAccount userAccountId:" + entity.getId());
		try {
			userAccountService.userAccountTermination(entity, entity.getTerminationDate(),
					entity.getTerminationReason(), getCurrentUser());
			messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));
		} catch (BusinessException e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
	}

	public String cancelAccount() {
		log.info("cancelAccount userAccountId:" + entity.getId());
		try {
			userAccountService.userAccountCancellation(entity, new Date(), getCurrentUser());
			messages.info(new BundleKey("messages", "cancellation.cancelSuccessful"));
			return "/pages/billing/userAccounts/userAccountDetail.xhtml?objectId=" + entity.getId() + "&edit=false";
		} catch (BusinessException e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String reactivateAccount() {
		log.info("reactivateAccount userAccountId:" + entity.getId());
		try {
			userAccountService.userAccountReactivation(entity, new Date(), getCurrentUser());
			messages.info(new BundleKey("messages", "reactivation.reactivateSuccessful"));
			return "/pages/billing/userAccounts/userAccountDetail.xhtml?objectId=" + entity.getId() + "&edit=false";
		} catch (BusinessException e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public LazyDataModel<WalletOperation> getWalletOperationsNoInvoiced() {
		log.debug("getWalletOperationsNoInvoiced");
		LazyDataModel<WalletOperation> result = null;
		HashMap<String, Object> filters = new HashMap<String, Object>();
		if (entity == null) {
			log.debug("getWalletOperationsNoInvoiced: userAccount is null");
			initEntity();
			log.debug("entity.id=" + entity.getId());
		}
		if (entity.getWallet() == null) {
			log.debug("getWalletOperationsNoInvoiced: userAccount " + entity.getId() + " has no wallet");
		} else {
			filters.put("wallet", entity.getWallet());
			filters.put("status", WalletOperationStatusEnum.OPEN);
			result = walletOperationBean.getLazyDataModel(filters, true);
		}
		return result;
	}

	public List<WalletOperation> getWalletOperations(String walletCode) {
		log.debug("getWalletOperations {}", walletCode);

		if (entity != null && entity.getProvider() != null) {
			return walletOperationService.findByUserAccountAndWalletCode(walletCode, entity, entity.getProvider(),
					false);
		}

		return null;
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
		if (billingAccount.getProvider() != null && billingAccount.getProvider().isLevelDuplication()) {
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

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "billingAccount", "billingAccount.customerAccount",
				"billingAccount.customerAccount.customer");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider", "billingAccount");
	}

	public WalletOperation getReloadOperation() {
		return reloadOperation;
	}

	public void setReloadOperation(WalletOperation reloadOperation) {
		this.reloadOperation = reloadOperation;
	}

	public String getSelectedWalletCode() {
		return selectedWalletCode;
	}

	public void setSelectedWalletCode(String selectedWalletCode) {
		this.selectedWalletCode = selectedWalletCode;
		this.reloadOperation = new WalletOperation();
		reloadOperation.setCode("RELOAD");
		reloadOperation.setOperationDate(new Date());
		reloadOperation.setQuantity(BigDecimal.ONE);
		reloadOperation.setCurrency(entity.getBillingAccount().getCustomerAccount().getTradingCurrency().getCurrency());
		reloadOperation.setWallet(entity.getWalletInstance(selectedWalletCode));
		reloadOperation.setDescription("reload");
		reloadOperation.setProvider(entity.getProvider());
		reloadOperation.setSeller(entity.getBillingAccount().getCustomerAccount().getCustomer().getSeller());
		reloadOperation.setStatus(WalletOperationStatusEnum.TREATED);
		reloadOperation.setType(OperationTypeEnum.CREDIT);
	}

	public void reload() {
		walletOperationService.create(reloadOperation, getCurrentUser());
		reloadOperation = null;
	}

	public String getBalance(WalletInstance wallet) {

		String result = null;
		BigDecimal balance = walletCacheContainerProvider.getBalance(wallet.getId());
		if (balance != null) {
			result = balance.toPlainString();
		}
		return result;
	}

	public String getReservedBalance(WalletInstance wallet) {
		String result = null;
		BigDecimal balance = walletCacheContainerProvider.getReservedBalance(wallet.getId());
		if (balance != null) {
			result = balance.toPlainString();
		}
		return result;
	}

	public String getOpenBalanceWithoutTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		String result = null;
		BigDecimal balance = walletReservationService.getOpenBalanceWithoutTax(provider, sellerCode, userAccountCode,
				startDate, endDate);
		if (balance != null) {
			result = balance.setScale(2, RoundingMode.HALF_UP).toPlainString();
		}
		return result;
	}

	public String getOpenBalanceWithTax(Provider provider, String sellerCode, String userAccountCode, Date startDate,
			Date endDate) throws BusinessException {
		String result = null;
		BigDecimal balance = walletReservationService.getOpenBalanceWithTax(provider, sellerCode, userAccountCode,
				startDate, endDate);
		if (balance != null) {
			result = balance.setScale(2, RoundingMode.HALF_UP).toPlainString();
		}
		return result;
	}

	public String getReservedBalanceWithoutTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		String result = null;
		BigDecimal balance = walletReservationService.getReservedBalanceWithoutTax(provider, sellerCode,
				userAccountCode, startDate, endDate);
		if (balance != null) {
			result = balance.setScale(2, RoundingMode.HALF_UP).toPlainString();
		}
		return result;
	}

	public String getReservedBalanceWithTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		String result = null;
		BigDecimal balance = walletReservationService.getReservedBalanceWithTax(provider, sellerCode, userAccountCode,
				startDate, endDate);
		if (balance != null) {
			result = balance.setScale(2, RoundingMode.HALF_UP).toPlainString();
		}
		return result;
	}

	public String getCurrentBalanceWithoutTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		String result = null;
		BigDecimal balance = walletReservationService.getCurrentBalanceWithoutTax(provider, sellerCode,
				userAccountCode, startDate, endDate);
		if (balance != null) {
			result = balance.setScale(2, RoundingMode.HALF_UP).toPlainString();
		}
		return result;
	}

	public String getCurrentBalanceWithTax(Provider provider, String sellerCode, String userAccountCode,
			Date startDate, Date endDate) throws BusinessException {
		String result = null;
		BigDecimal balance = walletReservationService.getCurrentBalanceWithTax(provider, sellerCode, userAccountCode,
				startDate, endDate);
		if (balance != null) {
			result = balance.setScale(2, RoundingMode.HALF_UP).toPlainString();
		}
		return result;
	}

	public List<SelectItem> getWalletOperationStatusList() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Faces.getLocale());

		List<SelectItem> filterLockedOptions = new ArrayList<SelectItem>(Arrays.asList(new SelectItem("OPEN",
				resourceBundle.getString("walletOperationStatus.open")),
				new SelectItem("TREATED", resourceBundle.getString("walletOperationStatus.treated")), new SelectItem(
						"CANCELED", resourceBundle.getString("walletOperationStatus.canceled")), new SelectItem(
						"RESERVED", resourceBundle.getString("walletOperationStatus.reserved")), new SelectItem(
						"TO_RERATE", resourceBundle.getString("walletOperationStatus.to_rerate"))));

		return filterLockedOptions;
	}


	 public CounterInstance getSelectedCounterInstance() {
		   if(entity==null){
		    initEntity();
		   }
		  return selectedCounterInstance;
		 }

	public void setSelectedCounterInstance(CounterInstance selectedCounterInstance) {
		this.selectedCounterInstance = selectedCounterInstance;
	}
	 
}