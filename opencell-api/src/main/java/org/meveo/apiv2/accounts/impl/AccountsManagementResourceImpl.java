package org.meveo.apiv2.accounts.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.accounts.ConsumerInput;
import org.meveo.apiv2.accounts.OpenTransactionsActionEnum;
import org.meveo.apiv2.accounts.ParentInput;
import org.meveo.apiv2.accounts.resource.AccountsManagementResource;
import org.meveo.apiv2.accounts.service.AccountsManagementApiService;

import com.fasterxml.jackson.core.JsonProcessingException;

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

    @Override
    public Response changeCustomerAccountParentAccount(String customerAccountCode, ParentInput parentInput) throws JsonProcessingException {
        accountsManagementApiService.changeCustomerAccountParentAccount(customerAccountCode, parentInput);
        return Response.noContent().build();
    }
}
