package org.meveo.apiv2.accounts.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.accounts.ConsumerInput;
import org.meveo.apiv2.accounts.OpenTransactionsActionEnum;
import org.meveo.apiv2.accounts.resource.AccountsManagementResource;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.WalletService;

public class AccountsManagementResourceImpl implements AccountsManagementResource {

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private WalletService walletService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Override
    public Response transferSubscription(String subscriptionCode, ConsumerInput consumerInput, OpenTransactionsActionEnum action) {

        // Check user account
        if (consumerInput == null || (consumerInput.getId() == null && StringUtils.isBlank(consumerInput.getCode()))) {
            return Response.status(403).entity("At least consumer id or code must be non-null").build();
        }

        if (consumerInput.getId() != null && StringUtils.isNotBlank(consumerInput.getCode())) {
            return Response.status(403).entity("Only one of parameters can be provided").build();
        }

        UserAccount newOwner = null;

        if (consumerInput.getId() != null) {
            newOwner = userAccountService.findById(consumerInput.getId());
            if (newOwner == null) {
                return Response.status(404).entity("user account {id=[id]} doesn't exist".replace("[id]", consumerInput.getId().toString())).build();
            }
        }

        if (StringUtils.isNotBlank(consumerInput.getCode())) {
            newOwner = userAccountService.findByCode(consumerInput.getCode());
            if (newOwner == null) {
                return Response.status(404).entity("user account {code=[code]} doesn't exist".replace("[code]", consumerInput.getCode())).build();
            }
        }

        // Check subscription
        Subscription subscription = subscriptionService.findByCode(subscriptionCode);

        if (subscription == null) {
            return Response.status(404).entity("Subscription {code=[code]} doesn't exist".replace("[code]", subscriptionCode)).build();
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CLOSED) {
            return Response.status(403)
                .entity("Cannot move a terminated subscription {id=[id], code=[code]}".replace("[id]", subscription.getId().toString()).replace("[code]", subscriptionCode))
                .build();
        }

        // Check WalletInstance
        WalletInstance newWallet = walletService.findByUserAccount(newOwner);
        if (newWallet == null) {
            return Response.status(404)
                .entity("wallet instance doesn't exist for user account {id=[id], code=[code]}".replace("[id]", newOwner.getId().toString()).replace("[code]", newOwner.getCode()))
                .build();
        }

        // Check action
        if (action == OpenTransactionsActionEnum.FAIL) {
            Long countWO = walletOperationService.countNotBilledWOBySubscription(subscription);
            if (countWO > 0) {
                return Response.status(403).entity("Cannot move subscription {id=[id], code=[code]} with OPEN wallet operations".replace("[id]", subscription.getId().toString())
                    .replace("[code]", subscriptionCode)).build();
            }

            Long countRT = ratedTransactionService.countNotBilledRTBySubscription(subscription);
            if (countRT > 0) {
                return Response.status(403).entity("Cannot move subscription {id=[id], code=[code]} with OPEN rated operations".replace("[id]", subscription.getId().toString())
                    .replace("[code]", subscriptionCode)).build();
            }
        }

        int count = 0;
        if (action == OpenTransactionsActionEnum.MOVE) {
            count += walletOperationService.moveNotBilledWOToUA(newWallet, subscription);
            count += ratedTransactionService.moveNotBilledRTToUA(newWallet, subscription);
        }

        if (action == OpenTransactionsActionEnum.MOVE_AND_RERATE) {
            count += walletOperationService.moveAndRerateNotBilledWOToUA(newWallet, subscription);
            count += ratedTransactionService.moveAndRerateNotBilledRTToUA(newWallet, subscription);
        }

        // Attache to new user account
        subscription.setUserAccount(newOwner);
        subscriptionService.updateOwner(subscription, newOwner);

        if (count > 0) {
            return Response.status(200).build();
        }
        return Response.status(204).build();
    }
}
