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

import java.math.BigDecimal;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.util.RandomStringUtils;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.local.CustomerServiceLocal;
import org.meveo.service.payments.local.CustomerAccountServiceLocal;

/**
 * Standard backing bean for {@link CustomerAccount} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Ignas
 * @created 2009.10.13
 */
@Name("customerAccountBean")
@Scope(ScopeType.CONVERSATION)
public class CustomerAccountBean extends BaseBean<CustomerAccount> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link CustomerAccount} service. Extends
	 * {@link PersistenceService}.
	 */
	@In(create = true)
	private CustomerAccountServiceLocal customerAccountService;

	/**
	 * Injected @{link Custome} service. Extends {@link PersistenceService}.
	 */
	@In
	private CustomerServiceLocal customerService;

	@In
	private User currentUser;

	private String selectedTab = "compte";

	/**
	 * Customer Id passed as a parameter. Used when creating new Customer
	 * Account from customer account window, so default customer account will be
	 * set on newly created customer Account.
	 */
	@RequestParameter
	private Long customerId;

	@RequestParameter
	private String tab;

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
	@Factory("customerAccount")
	@Begin(nested = true)
	public CustomerAccount init() {
		initEntity();
		if (entity.getId() == null) {
		    entity.setDateDunningLevel(new Date());
		    entity.setPassword(RandomStringUtils.randomAlphabetic(8));
		}
		if (entity.getId() == null && customerId != null) {
			Customer customer = customerService.findById(customerId);
			populateAccounts(customer);
		}
		if (tab != null) {
			selectedTab = tab;
		}
		return entity;
	}

	/**
	 * Data model of entities for data table in GUI.
	 * 
	 * @return filtered entities.
	 */
	@Out(value = "customerAccounts", required = false)
	protected PaginationDataModel<CustomerAccount> getDataModel() {
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
	@Factory("customerAccounts")
	@Begin(join = true)
	public void list() {
		super.list();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window (e.g if he came from customer window). Otherwise if user came from
	 * edit/new link, customer account saving does redirect him to same page in
	 * view mode.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	@End(beforeRedirect = true, root=false)
	public String saveOrUpdate() {
		try {

			if (entity.getDefaultLevel() != null
					&& entity.getDefaultLevel()) {
				if (customerAccountService.isDuplicationExist(entity)) {
				    entity.setDefaultLevel(false);
					throw new DuplicateDefaultAccountException();
				}

			}
			saveOrUpdate(entity);
			Redirect.instance().setParameter("edit", "false");
			Redirect.instance().setParameter("objectId",
			    entity.getId());
			Redirect.instance()
					.setViewId(
							"/pages/payments/customerAccounts/customerAccountDetail.xhtml");
			Redirect.instance().execute();

		} catch (DuplicateDefaultAccountException e1) {
			statusMessages.addFromResourceBundle(Severity.ERROR,
					"error.account.duplicateDefautlLevel");
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.addFromResourceBundle(Severity.ERROR,
					"javax.el.ELException");

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
			customerAccountService.transferAccount(entity,
					getCustomerAccountTransfer(), getAmountToTransfer(),
					currentUser);
			statusMessages.addFromResourceBundle("customerAccount.transfertOK");
			setCustomerAccountTransfer(null);
			setAmountToTransfer(BigDecimal.ZERO);

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.addFromResourceBundle("customerAccount.transfertKO");
		}

		return "/pages/payments/customerAccounts/customerAccountDetail.seam?objectId="
				+ entity.getId() + "&edit=false&tab=ops";
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
			return customerAccountService.customerAccountBalanceDue(
			    entity, new Date());
	}

	/**
	 * Compute balance exigible without litigation
	 * 
	 * @return exigible balance without litigation
	 * @throws BusinessException
	 */
	public BigDecimal getBalanceExigibleWithoutLitigation()
			throws BusinessException {
		if (entity.getId() == null) {
			return new BigDecimal(0);
		} else
			return customerAccountService
					.customerAccountBalanceExigibleWithoutLitigation(
					    entity, new Date());
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
			customerAccountService.closeCustomerAccount(entity,
					currentUser);
			statusMessages
					.addFromResourceBundle("customerAccount.closeSuccessful");

		} catch (BusinessException e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(e.getMessage());
		}
		return null;
	}

	/**
	 * @param customerAccountTransfer
	 *            the customerAccountTransfer to set
	 */
	public void setCustomerAccountTransfer(
			CustomerAccount customerAccountTransfer) {
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
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	/**
	 * @return the selectedTab
	 */
	public String getSelectedTab() {
		return selectedTab;
	}

	public void find() {
		try {
			customerAccountService.findByCode("1111");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void populateAccounts(Customer customer) {
	    entity.setCustomer(customer);
		if (customerAccountService.isDuplicationExist(entity)) {
		    entity.setDefaultLevel(false);
		} else {
		    entity.setDefaultLevel(true);
		}
		if (customer != null && customer.getProvider() != null
				&& customer.getProvider().isLevelDuplication()) {

		    entity.setCode(customer.getCode());
		    entity.setDescription(customer.getDescription());
		    entity.setAddress(customer.getAddress());
		    entity.setExternalRef1(customer.getExternalRef1());
		    entity.setExternalRef2(customer.getExternalRef2());
		    entity.setProviderContact(customer.getProviderContact());
		    entity.setName(customer.getName());
		    entity.setProvider(customer.getProvider());
		    entity.setPrimaryContact(customer.getPrimaryContact());
		    entity.setContactInformation(customer
					.getContactInformation());
		}
	}

}
