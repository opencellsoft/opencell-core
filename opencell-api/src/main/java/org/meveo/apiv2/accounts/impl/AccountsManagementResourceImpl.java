package org.meveo.apiv2.accounts.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.accounts.ConsumerInput;
import org.meveo.apiv2.accounts.OpenTransactionsActionEnum;
import org.meveo.apiv2.accounts.resource.AccountsManagementResource;
import org.meveo.apiv2.accounts.service.AccountsManagementApiService;

public class AccountsManagementResourceImpl implements AccountsManagementResource {

    @Inject
    private AccountsManagementApiService accountsManagementApiService;

    @Override
    public Response transferSubscription(String subscriptionCode, ConsumerInput consumerInput, OpenTransactionsActionEnum action) {

        int count = accountsManagementApiService.transferSubscription(subscriptionCode, consumerInput, action);
        if (count > 0) {
            return Response.ok().build();
        }
        return Response.noContent().build();
    }
}
