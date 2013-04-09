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
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

/**
 * Standard backing bean for {@link AccountEntity} that allows build accounts hierarchy for richfaces tree component. In this Bean you can set icons and links used in tree.
 * 
 * @author Gediminas Ubartas
 * @created Dec 14, 2010
 * 
 */
@Named
public class CustomerTreeBean extends BaseBean<AccountEntity> {

    private static final String SUBSCRIPTION_KEY = "subscription";

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link AccountEntity} service. Extends {@link PersistenceService}.
     */
    @Inject
    private AccountEntitySearchService accountEntitySearchService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CustomerTreeBean() {
        super(AccountEntity.class);
    }

    private long selected;

    /**
     * Override get instance method because AccountEntity is abstract class and can not be instantiated in {@link BaseBean}.
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
     * Build account hierarchy for richfaces tree component. Check entity type that was provided then loads {@link Customer} entity that is on top on hierarchy, and delegates
     * building logic to private build() recursion.
     */
    public TreeNode buildAccountsHierarchy(BaseEntity entity) {
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
            if (s.getUserAccount() != null && s.getUserAccount().getBillingAccount() != null && s.getUserAccount().getBillingAccount().getCustomerAccount() != null) {
                customer = s.getUserAccount().getBillingAccount().getCustomerAccount().getCustomer();
                accountEntitySearchService.refresh(s.getUserAccount());
            }
        }
        if (customer != null && customer.getCode() != null) {
            accountEntitySearchService.refresh(customer);
            return build(customer);
        } else {
            return null;
        }
    }

    /**
     * Builds accounts hierarchy for richfaces tree component. Customer has list of CustomerAccounts which has list of BillingAccounts which has list of UserAccounts which has list
     * of Susbcriptions. Any of those entities can be provided for this method and it will return remaining hierarchy in richfaces tree format.
     * 
     * @param entity Customer entity.
     * @return Richfaces tree hierarchy.
     */
    private TreeNode build(BaseEntity entity) {
        if (getObjectId() != null) {
            selected = getObjectId();
        }
        TreeNodeImpl root = null;

        if (entity instanceof Customer) {
//            TreeNodeImpl tree = new TreeNodeImpl();
            Customer customer = (Customer) entity;
            root= new TreeNodeData(customer.getId(), customer.getCode(), null, null, false, Customer.ACCOUNT_TYPE);
//            tree.addChild(0, root);
            List<CustomerAccount> customerAccounts = customer.getCustomerAccounts();
            for (int i = 0; i < customerAccounts.size(); i++) {
                root.addChild(i, build(customerAccounts.get(i)));
            }
            return root; // tree
        } else if (entity instanceof CustomerAccount) {
            CustomerAccount customerAccount = (CustomerAccount) entity;
            String firstName = (customerAccount.getName() != null && customerAccount.getName().getFirstName() != null) ? customerAccount.getName().getFirstName() : "";
            String lastName = (customerAccount.getName() != null && customerAccount.getName().getLastName() != null) ? customerAccount.getName().getLastName() : "";
            root =new TreeNodeData(customerAccount.getId(), customerAccount.getCode(), firstName, lastName, false, CustomerAccount.ACCOUNT_TYPE);
            List<BillingAccount> billingAccounts = customerAccount.getBillingAccounts();
            for (int i = 0; i < billingAccounts.size(); i++) {
                root.addChild(i, build(billingAccounts.get(i)));
            }
            return root;
        } else if (entity instanceof BillingAccount) {
            BillingAccount billingAccount = (BillingAccount) entity;

            String firstName = (billingAccount.getName() != null && billingAccount.getName().getFirstName() != null) ? billingAccount.getName().getFirstName() : "";
            String lastName = (billingAccount.getName() != null && billingAccount.getName().getLastName() != null) ? billingAccount.getName().getLastName() : "";
            root =new TreeNodeData(billingAccount.getId(), billingAccount.getCode(), firstName, lastName, false, BillingAccount.ACCOUNT_TYPE);
            List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
            for (int i = 0; i < userAccounts.size(); i++) {
                root.addChild(i, build(userAccounts.get(i)));
            }
            return root;
        } else if (entity instanceof UserAccount) {
            UserAccount userAccount = (UserAccount) entity;
            String firstName = (userAccount.getName() != null && userAccount.getName().getFirstName() != null) ? userAccount.getName().getFirstName() : "";
            String lastName = (userAccount.getName() != null && userAccount.getName().getLastName() != null) ? userAccount.getName().getLastName() : "";
            root =new TreeNodeData(userAccount.getId(), userAccount.getCode(), firstName, lastName, false, UserAccount.ACCOUNT_TYPE);
            List<Subscription> subscriptions = userAccount.getSubscriptions();
            if (subscriptions != null) {
                for (int i = 0; i < subscriptions.size(); i++) {
                    root.addChild(i, build(subscriptions.get(i)));
                }
            }
            return root;
        } else if (entity instanceof Subscription) {
            Subscription subscription = (Subscription) entity;
            root =new TreeNodeData(subscription.getId(), subscription.getCode(), null, null, false, SUBSCRIPTION_KEY);
            return root;
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
     * Richfaces needs el expression for tree 'adviseNodeOpened' parameter (expands tree when loading). Maybe its somehow possible to not call backing bean.
     */
    public boolean adviseNodeOpened() {
        return true;
    }

    /**
     * Because in customer search any type of customer can appear, this method is used in UI to get link to concrete customer edit page.
     * 
     * @param type Account type of Customer
     * 
     * @return Edit page url.
     */
    public String getView(String type) {
        if (type.equals(Customer.ACCOUNT_TYPE)) {
            return "/pages/crm/customers/customerDetail.xhtml";
        } else if (type.equals(CustomerAccount.ACCOUNT_TYPE)) {
            return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?conversationPropagation=end";
        }
        if (type.equals(BillingAccount.ACCOUNT_TYPE)) {
            return "/pages/billing/billingAccounts/billingAccountDetail.xhtml";
        }
        if (type.equals(UserAccount.ACCOUNT_TYPE)) {
            return "/pages/billing/userAccounts/userAccountDetail.xhtml";
        } else {
            throw new IllegalStateException("Wrong customer type provided in EL in .xhtml");
        }
    }

    // TODO
    // /**
    // * @see org.richfaces.event.NodeSelectedListener#processSelection(org.richfaces.event.NodeSelectedEvent)
    // */
    // public String processSelection(NodeSelectedEvent event) {
    // HtmlTree tree = ((HtmlTree) event.getComponent());
    // TreeNodeData selectedNodeData = (TreeNodeData) tree.getRowData();
    // selected = selectedNodeData.getId();
    // if (selectedNodeData.getType().equals(SUBSCRIPTION_KEY)) {
    // return "/pages/billing/subscriptions/subscriptionDetail.xhtml?objectId="
    // + selectedNodeData.getId() + "&faces-redirect=true";
    // } else {
    // String view = getView(selectedNodeData.getType());
    // return view + "?objectId=" + selectedNodeData.getId() + "&faces-redirect=true";
    // }
    // }

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
    public class TreeNodeData extends TreeNodeImpl{
        private Long id;
        private String code;
        private String firstName;
        private String lastName;
        /**
         * Flag for toString() method to know if it needs to show firstName/lastName.
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
}
