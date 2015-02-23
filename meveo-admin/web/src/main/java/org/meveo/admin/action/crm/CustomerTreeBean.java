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
package org.meveo.admin.action.crm;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.crm.impl.AccountEntitySearchService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * Standard backing bean for {@link AccountEntity} that allows build accounts
 * hierarchy for richfaces tree component. In this Bean you can set icons and
 * links used in tree.
 */
@Named
public class CustomerTreeBean extends BaseBean<AccountEntity> {

	private static final String SUBSCRIPTION_KEY = "subscription";
	private static final String ACCESS_KEY = "access";

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link AccountEntity} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private AccountEntitySearchService accountEntitySearchService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CustomerTreeBean() {
		super(AccountEntity.class);
	}

	@Inject
	@RequestParam
	private Instance<Long> customerId;

	@Inject
	@RequestParam
	private Instance<Long> customerAccountId;

	@Inject
	@RequestParam
	private Instance<Long> billingAccountId;

	@Inject
	@RequestParam
	private Instance<Long> userAccountId;

	@Inject
	@RequestParam
	private Instance<Long> subscriptionId;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private AccessService accessService;

	// private TreeNodeData selectedNode;

	/**
	 * Override get instance method because AccountEntity is abstract class and
	 * can not be instantiated in {@link BaseBean}.
	 */
	@Override
	public AccountEntity getInstance() throws InstantiationException, IllegalAccessException {
		return new AccountEntity() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getAccountType() {
				return "";
			}
		};
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<AccountEntity> getPersistenceService() {
		return accountEntitySearchService;
	}

	/**
	 * Build account hierarchy for Primefaces tree component. Check entity type
	 * that was provided then loads {@link Customer} entity that is on top on
	 * hierarchy, and delegates building logic to private build() recursion.
	 */
	public TreeNode buildAccountsHierarchy(BaseEntity entity) {

		if (entity.isTransient())
			return null;

		Customer customer = null;

		if (entity instanceof Customer) {
			customer = (Customer) entity;
		} else if (entity instanceof CustomerAccount) {
			CustomerAccount acc = (CustomerAccount) entity;
			customer = acc.getCustomer();
		} else if (entity instanceof BillingAccount) {
			BillingAccount acc = (BillingAccount) entity;
			// this kind of check is not really necessary, because tree
			// hierarchy should not be shown when creating new page
			if (acc.getCustomerAccount() != null) {
				customer = acc.getCustomerAccount().getCustomer();
			}
		} else if (entity instanceof UserAccount) {
			UserAccount acc = (UserAccount) entity;
			if (acc.getBillingAccount() != null && acc.getBillingAccount().getCustomerAccount() != null) {
				customer = acc.getBillingAccount().getCustomerAccount().getCustomer();
			}
		} else if (entity instanceof Subscription) {
			Subscription s = (Subscription) entity;
			if (s.getUserAccount() != null && s.getUserAccount().getBillingAccount() != null
					&& s.getUserAccount().getBillingAccount().getCustomerAccount() != null) {
				customer = s.getUserAccount().getBillingAccount().getCustomerAccount().getCustomer();
			}
		}

		if (customer != null && customer.getCode() != null) {
			return build(customer);
		} else {
			return null;
		}
	}

	private TreeNode build(BaseEntity entity) {
		TreeNode tree = new DefaultTreeNode("Root", null);
		return build(entity, tree);
	}

	/**
	 * Builds accounts hierarchy for Primefaces tree component. Customer has
	 * list of CustomerAccounts which has list of BillingAccounts which has list
	 * of UserAccounts which has list of Susbcriptions. Any of those entities
	 * can be provided for this method and it will return remaining hierarchy in
	 * Primefaces tree format.
	 * 
	 * @param entity
	 *            Customer entity.
	 * @return Primefaces tree hierarchy.
	 */
	private TreeNode build(BaseEntity entity, TreeNode parent) {

		if (entity instanceof Customer) {
			Customer customer = (Customer) entity;
			TreeNodeData treeNodeData = new TreeNodeData(customer.getId(), customer.getCode(), null, null, false,
					Customer.ACCOUNT_TYPE, customer.getId().equals(customerId.get()));
			TreeNode treeNode = new DefaultTreeNode(Customer.ACCOUNT_TYPE, treeNodeData, parent);
			if (treeNodeData.isSelected()) {
				expandTreeNode(treeNode);
			}

			List<CustomerAccount> customerAccounts = customerAccountService.listByCustomer(customer);
			if (customerAccounts != null) {
				for (int i = 0; i < customerAccounts.size(); i++) {
					build(customerAccounts.get(i), treeNode);
				}
			}

			return parent;
		} else if (entity instanceof CustomerAccount) {
			CustomerAccount customerAccount = (CustomerAccount) entity;
			String firstName = (customerAccount.getName() != null && customerAccount.getName().getFirstName() != null) ? customerAccount
					.getName().getFirstName() : "";
			String lastName = (customerAccount.getName() != null && customerAccount.getName().getLastName() != null) ? customerAccount
					.getName().getLastName() : "";
			TreeNodeData treeNodeData = new TreeNodeData(customerAccount.getId(), customerAccount.getCode(), firstName,
					lastName, false, CustomerAccount.ACCOUNT_TYPE, customerAccount.getId().equals(
							customerAccountId.get()));
			TreeNode treeNode = new DefaultTreeNode(CustomerAccount.ACCOUNT_TYPE, treeNodeData, parent);
			if (treeNodeData.isSelected()) {
				expandTreeNode(treeNode);
			}

			List<BillingAccount> billingAccounts = billingAccountService.listByCustomerAccount(customerAccount);
			if (billingAccounts != null) {
				for (int i = 0; i < billingAccounts.size(); i++) {
					build(billingAccounts.get(i), treeNode);
				}
			}

			return parent;
		} else if (entity instanceof BillingAccount) {
			BillingAccount billingAccount = (BillingAccount) entity;

			String firstName = (billingAccount.getName() != null && billingAccount.getName().getFirstName() != null) ? billingAccount
					.getName().getFirstName() : "";
			String lastName = (billingAccount.getName() != null && billingAccount.getName().getLastName() != null) ? billingAccount
					.getName().getLastName() : "";
			TreeNodeData treeNodeData = new TreeNodeData(billingAccount.getId(), billingAccount.getCode(), firstName,
					lastName, false, BillingAccount.ACCOUNT_TYPE, billingAccount.getId().equals(billingAccountId.get()));
			TreeNode treeNode = new DefaultTreeNode(BillingAccount.ACCOUNT_TYPE, treeNodeData, parent);
			if (treeNodeData.isSelected()) {
				expandTreeNode(treeNode);
			}

			List<UserAccount> userAccounts = userAccountService.listByBillingAccount(billingAccount);
			if (userAccounts != null) {
				for (int i = 0; i < userAccounts.size(); i++) {
					build(userAccounts.get(i), treeNode);
				}
			}

			return parent;
		} else if (entity instanceof UserAccount) {
			UserAccount userAccount = (UserAccount) entity;
			String firstName = (userAccount.getName() != null && userAccount.getName().getFirstName() != null) ? userAccount
					.getName().getFirstName() : "";
			String lastName = (userAccount.getName() != null && userAccount.getName().getLastName() != null) ? userAccount
					.getName().getLastName() : "";
			TreeNodeData treeNodeData = new TreeNodeData(userAccount.getId(), userAccount.getCode(), firstName,
					lastName, false, UserAccount.ACCOUNT_TYPE, userAccount.getId().equals(userAccountId.get()));
			TreeNode treeNode = new DefaultTreeNode(UserAccount.ACCOUNT_TYPE, treeNodeData, parent);
			if (treeNodeData.isSelected()) {
				expandTreeNode(treeNode);
			}

			List<Subscription> subscriptions = subscriptionService.listByUserAccount(userAccount);
			if (subscriptions != null) {
				if (subscriptions != null) {
					for (int i = 0; i < subscriptions.size(); i++) {
						build(subscriptions.get(i), treeNode);
					}
				}
			}

			return parent;
		} else if (entity instanceof Subscription) {
			Subscription subscription = (Subscription) entity;
			TreeNodeData treeNodeData = new TreeNodeData(subscription.getId(), subscription.getCode(), null, null,
					false, SUBSCRIPTION_KEY, subscription.getId().equals(subscriptionId.get()));
			TreeNode treeNode = new DefaultTreeNode(SUBSCRIPTION_KEY, treeNodeData, parent);
			if (treeNodeData.isSelected()) {
				expandTreeNode(treeNode);
			}

			List<Access> accesses = accessService.listBySubscription(subscription);
			if (accesses != null) {
				if (accesses != null) {
					for (int i = 0; i < accesses.size(); i++) {
						build(accesses.get(i), treeNode);
					}
				}
			}

			return parent;
		} else if (entity instanceof Access) {
			Access access = (Access) entity;
			TreeNodeData treeNodeData = new TreeNodeData(access.getSubscription().getId(), access.getAccessUserId(),
					null, null, false, ACCESS_KEY, access.getSubscription().getId().equals(subscriptionId.get()));
			TreeNode treeNode = new DefaultTreeNode(ACCESS_KEY, treeNodeData, parent);

			if (treeNodeData.isSelected()) {
				expandTreeNode(treeNode);
			}

			return parent;
		}

		throw new IllegalStateException("Unsupported entity for hierarchy");
	}

	private void expandTreeNode(TreeNode treeNode) {
		treeNode.setExpanded(true);

		if (treeNode.getParent() != null) {
			expandTreeNode(treeNode.getParent());
		}
	}

	public String getIcon(String type) {
		if (type.equals(Customer.ACCOUNT_TYPE)) {
			return "/img/customer-icon.png";
		}

		if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
			return "/img/customerAccount-icon.png";
		}

		if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
			return "/img/billingAccount-icon.png";
		}

		if (type.equals(UserAccount.ACCOUNT_TYPE)) {
			return "/img/userAccount-icon.png";
		}

		if (type.equals(SUBSCRIPTION_KEY)) {
			return "/img/subscription-icon.gif";
		}

		if (type.equals(ACCESS_KEY)) {
			return "/img/subscription-icon.gif";
		}

		return null;
	}

	public class TreeNodeData {
		private Long id;
		private String code;
		private String firstName;
		private String lastName;
		/**
		 * Flag for toString() method to know if it needs to show
		 * firstName/lastName.
		 */
		private boolean showName;
		private String type;
		private boolean selected;

		public TreeNodeData(Long id, String code, String firstName, String lastName, boolean showName, String type,
				boolean selected) {
			super();
			this.id = id;
			this.code = code;
			this.firstName = firstName;
			this.lastName = lastName;
			this.showName = showName;
			this.type = type;
			this.selected = selected;
		}

		public Long getId() {
			return id;
		}

		public String getCode() {
			return code;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public String getType() {
			return type;
		}

		public String getFirstAndLastName() {
			String result = lastName;
			if (firstName != null) {
				result = firstName + " " + lastName;
			}
			return result;
		}

		@Override
		public String toString() {
			if (!StringUtils.isBlank(code)) {
				StringBuilder builder = new StringBuilder(code);
				if (showName) {
					builder.append(" ").append(firstName).append(" ").append(lastName);
				}
				return builder.toString();
			} else {
				return "";
			}
		}

		/**
		 * Because in customer search any type of customer can appear, this
		 * method is used in UI to get link to concrete customer edit page.
		 * 
		 * @param type
		 *            Account type of Customer
		 * 
		 * @return Edit page url.
		 */
		public String getView() {
			if (type.equals(Customer.ACCOUNT_TYPE)) {
				return "customerDetail";
			} else if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
				return "customerAccountDetail";
			} else if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
				return "billingAccountDetail";
			} else if (type.equals(UserAccount.ACCOUNT_TYPE)) {
				return "userAccountDetail";
			} else if (type.equals(SUBSCRIPTION_KEY)) {
				return "subscriptionDetail";
			} else if (type.equals(ACCESS_KEY)) {
				return "access";
			} else {
				throw new IllegalStateException("Wrong customer type " + type + " provided in EL in .xhtml");
			}
		}

		public boolean isSelected() {
			return selected;
		}
	}

}