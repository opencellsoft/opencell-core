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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.RandomStringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.AccountBean;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldEnabledBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link CustomerAccount} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
@CustomFieldEnabledBean(accountLevel=AccountLevelEnum.CA)
public class CustomerAccountBean extends AccountBean<CustomerAccount> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link CustomerAccount} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private CustomerAccountService customerAccountService;

	/**
	 * Injected @{link Custome} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private CustomerService customerService;

	private int selectedTab = 0;

	/**
	 * Customer Id passed as a parameter. Used when creating new Customer
	 * Account from customer account window, so default customer account will be
	 * set on newly created customer Account.
	 */
	private Long customerId;

	@RequestParam
	private Instance<Integer> tab;

	private CustomerAccount customerAccountTransfer = new CustomerAccount();

	private BigDecimal amountToTransfer;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CustomerAccountBean() {
		super(CustomerAccount.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public CustomerAccount initEntity() {
		super.initEntity();
		if (entity.getId() == null) {
			entity.setDateDunningLevel(new Date());
			entity.setPassword(RandomStringUtils.randomAlphabetic(8));
		}
		if (entity.getId() == null && getCustomerId() != null) {
			Customer customer = customerService.findById(getCustomerId());
			populateAccounts(customer);

			// check if has default
			if (!customer.getDefaultLevel()) {
				entity.setDefaultLevel(true);
			}
		}
		if (getTab() != null) {
			selectedTab = getTab();
		}

		return entity;
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window (e.g if he came from customer window). Otherwise if user came from
	 * edit/new link, customer account saving does redirect him to same page in
	 * view mode.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation) {
		try {
			if (entity.getDefaultLevel() != null && entity.getDefaultLevel()) {
				if (customerAccountService.isDuplicationExist(entity)) {
					entity.setDefaultLevel(false);
					throw new DuplicateDefaultAccountException();
				}
			}

			super.saveOrUpdate(killConversation);

			log.debug("isAttached={}", getPersistenceService().getEntityManager().contains(entity));

			return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?edit=false&customerAccountId="
					+ entity.getId() + "&faces-redirect=true&includeViewParams=true";
		} catch (DuplicateDefaultAccountException e1) {
			messages.error(new BundleKey("messages", "error.account.duplicateDefautlLevel"));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(new BundleKey("messages", "javax.el.ELException"));

		}

		return null;
	}

	/**
	 * Move selected accountOperation from current CustomerAccount to
	 * customerAccountTo
	 * 
	 * @param customerAccountTo
	 * @return
	 */
	public String transferAccount() {
		try {
			customerAccountService.transferAccount(entity, getCustomerAccountTransfer(), getAmountToTransfer(),
					getCurrentUser());
			messages.info(new BundleKey("messages", "customerAccount.transfertOK"));
			setCustomerAccountTransfer(null);
			setAmountToTransfer(BigDecimal.ZERO);

		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(new BundleKey("messages", "customerAccount.transfertKO"));
		}

		return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId=" + entity.getId()
				+ "&edit=false&tab=ops&faces-redirect=true";
	}

	public String backCA() {

		return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId=" + entity.getId()
				+ "&edit=false&tab=ops&faces-redirect=true";
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<CustomerAccount> getPersistenceService() {
		return customerAccountService;
	}

	/**
	 * Compute balance due
	 * 
	 * @return due balance
	 * @throws BusinessException
	 */
	public BigDecimal getBalanceDue() throws BusinessException {
		if (entity.getId() == null) {
			return new BigDecimal(0);
		} else
			return customerAccountService.customerAccountBalanceDue(entity, new Date());
	}

	/**
	 * Compute balance exigible without litigation
	 * 
	 * @return exigible balance without litigation
	 * @throws BusinessException
	 */
	public BigDecimal getBalanceExigibleWithoutLitigation() throws BusinessException {
		if (entity.getId() == null) {
			return new BigDecimal(0);
		} else
			return customerAccountService.customerAccountBalanceExigibleWithoutLitigation(entity, new Date());
	}

	/**
	 * is current customerAccount active
	 */
	public boolean isActiveAccount() {
		if (entity != null) {
			return entity.getStatus() == CustomerAccountStatusEnum.ACTIVE;
		}
		return false;
	}

	/**
	 * Close customerAccount
	 * 
	 * @return
	 */
	public String closeCustomerAccount() {
		log.info("closeAccount customerAccountId:" + entity.getId());
		try {
			customerAccountService.closeCustomerAccount(entity, getCurrentUser());
			messages.info(new BundleKey("messages", "customerAccount.closeSuccessful"));
		} catch (BusinessException e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	/**
	 * @param customerAccountTransfer
	 *            the customerAccountTransfer to set
	 */
	public void setCustomerAccountTransfer(CustomerAccount customerAccountTransfer) {
		this.customerAccountTransfer = customerAccountTransfer;
	}

	/**
	 * @return the customerAccountTransfer
	 */
	public CustomerAccount getCustomerAccountTransfer() {
		return customerAccountTransfer;
	}

	/**
	 * @param amountToTransfer
	 *            the amountToTransfer to set
	 */
	public void setAmountToTransfer(BigDecimal amountToTransfer) {
		this.amountToTransfer = amountToTransfer;
	}

	/**
	 * @return the amountToTransfer
	 */
	public BigDecimal getAmountToTransfer() {
		return amountToTransfer;
	}

	/**
	 * @param selectedTab
	 *            the selectedTab to set
	 */
	public void setSelectedTab(int selectedTab) {
		this.selectedTab = selectedTab;
	}

	/**
	 * @return the selectedTab
	 */
	public int getSelectedTab() {
		return selectedTab;
	}

	public void populateAccounts(Customer customer) {
		entity.setCustomer(customer);
		if (customerAccountService.isDuplicationExist(entity)) {
			entity.setDefaultLevel(false);
		} else {
			entity.setDefaultLevel(true);
		}
		if (customer != null && customer.getProvider() != null && customer.getProvider().isLevelDuplication()) {

			entity.setCode(customer.getCode());
			entity.setDescription(customer.getDescription());
			entity.setAddress(customer.getAddress());
			entity.setExternalRef1(customer.getExternalRef1());
			entity.setExternalRef2(customer.getExternalRef2());
			entity.setProviderContact(customer.getProviderContact());
			entity.setName(customer.getName());
			entity.setProvider(customer.getProvider());
			entity.setPrimaryContact(customer.getPrimaryContact());
			entity.setContactInformation(customer.getContactInformation());
			entity.setMandateIdentification(customer.getMandateIdentification());
			entity.setMandateDate(customer.getMandateDate());
		}
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	private Integer getTab() {
		if (tab != null) {
			return tab.get();
		}
		return null;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "customer");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider", "customer");
	}
}