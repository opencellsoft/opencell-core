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
package org.meveo.admin.action.billing;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.UserAccountService;

/**
 * Standard backing bean for {@link UserAccount} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
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
    private UserAccountService userAccountService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    @RequestParam
    private Instance<Long> billingAccountId;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private Messages messages;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public UserAccountBean() {
        super(UserAccount.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Override
    public UserAccount initEntity() {
        super.initEntity();
        if (entity.getId() == null && billingAccountId != null) {
            BillingAccount billingAccount = billingAccountService.findById(billingAccountId.get());
            entity.setBillingAccount(billingAccount);
            populateAccounts(billingAccount);
        }
        return entity;
    }


    /**
     * Conversation is ended and user is redirected from edit to his previous window.
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
     */
    // @End(beforeRedirect = true, root=false)
    public String saveOrUpdate() {
        try {
            if (entity.getDefaultLevel()) {
                if (userAccountService.isDuplicationExist(entity)) {
                    entity.setDefaultLevel(false);
                    throw new DuplicateDefaultAccountException();
                }

            }
            saveOrUpdate(entity);
            return "/pages/billing/userAccounts/userAccountDetail.xhtml?edit=false&objectId=" + entity.getId() + " &faces-redirect=true";
        } catch (DuplicateDefaultAccountException e1) {
            messages.error(new BundleKey("messages", "error.account.duplicateDefautlLevel"));
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

    public String saveOrUpdate(UserAccount entity) {
        try {
            if (entity.isTransient()) {
                userAccountService.createUserAccount(entity.getBillingAccount().getCode(), entity, getCurrentUser());
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

    public String terminateAccount() {
        log.info("resiliateAccount userAccountId:" + entity.getId());
        try {
            userAccountService.userAccountTermination(entity.getCode(), new Date(), getCurrentUser());
            messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));
            return "/pages/billing/userAccounts/userAccountDetail.xhtml?objectId=" + entity.getId() + "&edit=false";
        } catch (BusinessException e) {
            e.printStackTrace();
            messages.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messages.error(e.getMessage());
        }
        return null;
    }

    public String cancelAccount() {
        log.info("cancelAccount userAccountId:" + entity.getId());
        try {
            userAccountService.userAccountCancellation(entity.getCode(), new Date(), getCurrentUser());
            messages.info(new BundleKey("messages", "cancellation.cancelSuccessful"));
            return "/pages/billing/userAccounts/userAccountDetail.xhtml?objectId=" + entity.getId() + "&edit=false";
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
            userAccountService.userAccountReactivation(entity.getCode(), new Date(), getCurrentUser());
            messages.info(new BundleKey("messages", "reactivation.reactivateSuccessful"));
            return "/pages/billing/userAccounts/userAccountDetail.xhtml?objectId=" + entity.getId() + "&edit=false";
        } catch (BusinessException e) {
            e.printStackTrace(); // TODO WTF printStackTrace??
            messages.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            messages.error(e.getMessage());
        }
        return null;
    }

    @Produces
    @Named("getRatedTransactionsNoInvoiced")
    public List<RatedTransaction> getRatedTransactionsNoInvoiced() {
        return ratedTransactionService.getRatedTransactionsNoInvoiced(entity);
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

}
