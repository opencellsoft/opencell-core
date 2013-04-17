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
package org.meveo.admin.action.crm;

import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.AccountEntity;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.AccountEntitySearchService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;

/**
 * Standard backing bean for {@link AccountEntity} that allows build accounts
 * hierarchy for richfaces tree component. In this Bean you can set icons and
 * links used in tree.
 */
@Named
public class CustomerTreeBean extends BaseBean<AccountEntity> {

	private static final String SUBSCRIPTION_KEY = "subscription";

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link AccountEntity} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private AccountEntitySearchService accountEntitySearchService;

	@Inject
	private Logger log;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CustomerTreeBean() {
		super(AccountEntity.class);
	}

	private long selected;

	private TreeNodeData selectedNode;

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
		Customer customer = null;
		log.debug("AKK about to build tree hierarchy");

		if (entity instanceof Customer) {
			customer = (Customer) entity;
		} else if (entity instanceof CustomerAccount) {
			CustomerAccount acc = (CustomerAccount) entity;
			log.debug("AKK about to get customer");
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
				accountEntitySearchService.refresh(s.getUserAccount());
			}
		}
		log.debug("AKK customer acount is " + customer);
		if (customer != null && customer.getCode() != null) {
			accountEntitySearchService.refresh(customer);
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
		if (getObjectId() != null) {
			selected = getObjectId();
		}

		if (entity instanceof Customer) {
			Customer customer = (Customer) entity;
			TreeNodeData treeNodeData = new TreeNodeData(customer.getId(), customer.getCode(), null, null, false,
					Customer.ACCOUNT_TYPE);
			TreeNode treeNode = new DefaultTreeNode(Customer.ACCOUNT_TYPE, treeNodeData, parent);
			List<CustomerAccount> customerAccounts = customer.getCustomerAccounts();

			for (int i = 0; i < customerAccounts.size(); i++) {
				build(customerAccounts.get(i), treeNode);
			}

			return parent;
		} else if (entity instanceof CustomerAccount) {
			CustomerAccount customerAccount = (CustomerAccount) entity;
			String firstName = (customerAccount.getName() != null && customerAccount.getName().getFirstName() != null) ? customerAccount
					.getName().getFirstName() : "";
			String lastName = (customerAccount.getName() != null && customerAccount.getName().getLastName() != null) ? customerAccount
					.getName().getLastName() : "";
			TreeNodeData treeNodeData = new TreeNodeData(customerAccount.getId(), customerAccount.getCode(), firstName,
					lastName, false, CustomerAccount.ACCOUNT_TYPE);
			TreeNode treeNode = new DefaultTreeNode(CustomerAccount.ACCOUNT_TYPE, treeNodeData, parent);
			List<BillingAccount> billingAccounts = customerAccount.getBillingAccounts();

			for (int i = 0; i < billingAccounts.size(); i++) {
				build(billingAccounts.get(i), treeNode);
			}

			return parent;
		} else if (entity instanceof BillingAccount) {
			BillingAccount billingAccount = (BillingAccount) entity;

			String firstName = (billingAccount.getName() != null && billingAccount.getName().getFirstName() != null) ? billingAccount
					.getName().getFirstName() : "";
			String lastName = (billingAccount.getName() != null && billingAccount.getName().getLastName() != null) ? billingAccount
					.getName().getLastName() : "";
			TreeNodeData treeNodeData = new TreeNodeData(billingAccount.getId(), billingAccount.getCode(), firstName,
					lastName, false, BillingAccount.ACCOUNT_TYPE);
			TreeNode treeNode = new DefaultTreeNode(BillingAccount.ACCOUNT_TYPE, treeNodeData, parent);
			List<UserAccount> userAccounts = billingAccount.getUsersAccounts();

			for (int i = 0; i < userAccounts.size(); i++) {
				build(userAccounts.get(i), treeNode);
			}

			return parent;
		} else if (entity instanceof UserAccount) {
			UserAccount userAccount = (UserAccount) entity;
			String firstName = (userAccount.getName() != null && userAccount.getName().getFirstName() != null) ? userAccount
					.getName().getFirstName() : "";
			String lastName = (userAccount.getName() != null && userAccount.getName().getLastName() != null) ? userAccount
					.getName().getLastName() : "";
			TreeNodeData treeNodeData = new TreeNodeData(userAccount.getId(), userAccount.getCode(), firstName,
					lastName, false, UserAccount.ACCOUNT_TYPE);
			TreeNode treeNode = new DefaultTreeNode(UserAccount.ACCOUNT_TYPE, treeNodeData, parent);
			List<Subscription> subscriptions = userAccount.getSubscriptions();

			if (subscriptions != null) {
				for (int i = 0; i < subscriptions.size(); i++) {
					build(subscriptions.get(i), treeNode);
				}
			}

			return parent;
		} else if (entity instanceof Subscription) {
			Subscription subscription = (Subscription) entity;
			TreeNodeData treeNodeData = new TreeNodeData(subscription.getId(), subscription.getCode(), null, null,
					false, SUBSCRIPTION_KEY);
			new DefaultTreeNode(SUBSCRIPTION_KEY, treeNodeData, parent);

			return parent;
		}

		throw new IllegalStateException("Unsupported entity for hierarchy");
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
		return null;
	}

	/**
	 * Because in customer search any type of customer can appear, this method
	 * is used in UI to get link to concrete customer edit page.
	 * 
	 * @param type
	 *            Account type of Customer
	 * 
	 * @return Edit page url.
	 */
	public String getView(String type, String id) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		String rootURL = ec.getRequestContextPath();

		if (type.equals(Customer.ACCOUNT_TYPE)) {
			return rootURL + "/pages/crm/customers/customerDetail.jsf?edit=false&customerId=" + id + "&cid="
					+ conversation.getId();
		} else if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
			return rootURL + "/pages/payments/customerAccounts/customerAccountDetail.jsf?edit=false&customerAccountId=" + id
					+ "&cid=" + conversation.getId();
		}
		if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
			return rootURL + "/pages/billing/billingAccounts/billingAccountDetail.jsf?edit=false&billingAccountId=" + id
					+ "&cid=" + conversation.getId();
		}
		if (type.equals(UserAccount.ACCOUNT_TYPE)) {
			return rootURL + "/pages/billing/userAccounts/userAccountDetail.jsf?edit=false&userAccountId=" + id + "&cid="
					+ conversation.getId();
		} else {
			throw new IllegalStateException("Wrong customer type provided in EL in .xhtml");
		}
	}

	/**
	 * @see org.richfaces.event.NodeSelectedListener#processSelection(org.richfaces.event.NodeSelectedEvent)
	 */
	/*
	 * public String processSelection(TreeSelectionChangeEvent event) {
	 * TreeNodeData selectedNodeData = (TreeNodeData) event.getNewSelection();
	 * selected = selectedNodeData.getId(); if
	 * (selectedNodeData.getType().equals(SUBSCRIPTION_KEY)) { return
	 * "/pages/billing/subscriptions/subscriptionDetail.xhtml?objectId=" +
	 * selectedNodeData.getId() + "&faces-redirect=true"; } else { String view =
	 * getView(selectedNodeData.getType()); return view + "?objectId=" +
	 * selectedNodeData.getId() + "&faces-redirect=true"; } }
	 */

	public long getSelected() {
		return selected;
	}

	public void setSelected(long selected) {
		this.selected = selected;
	}

	public String getEntityType(BaseEntity entity) {
		String type = "";
		if (entity instanceof AccountEntity) {
			type = ((AccountEntity) entity).getAccountType();
		} else if (entity instanceof Subscription) {
			type = SUBSCRIPTION_KEY;
		}
		return type;
	}

	/**
	 * Data holder class for tree hierarchy.
	 * 
	 * @author Ignas Lelys
	 * @created Dec 8, 2010
	 * 
	 */
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

		public TreeNodeData(Long id, String code, String firstName, String lastName, boolean showName, String type) {
			super();
			this.id = id;
			this.code = code;
			this.firstName = firstName;
			this.lastName = lastName;
			this.showName = showName;
			this.type = type;
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
			StringBuilder builder = new StringBuilder(code);
			if (showName) {
				builder.append(" ").append(firstName).append(" ").append(lastName);
			}
			return builder.toString();
		}
	}

	public TreeNodeData getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNodeData selectedNode) {
		this.selectedNode = selectedNode;
	}
}
