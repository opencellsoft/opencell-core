package org.meveo.apiv2.accounts.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.apiv2.accounts.ConsumerInput;
import org.meveo.apiv2.accounts.CounterInstanceDto;
import org.meveo.apiv2.accounts.OpenTransactionsActionEnum;
import org.meveo.apiv2.accounts.ParentInput;
import org.meveo.apiv2.accounts.resource.AccountsManagementResource;
import org.meveo.apiv2.accounts.service.AccountsManagementApiService;
import org.meveo.apiv2.ordering.common.LinkGenerator;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

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

    @Override
    public Response createCounterInstance(CounterInstanceDto dto) {
        Long newInstanceId = accountsManagementApiService.createCounterInstance(dto);
        ActionStatus createdStatus = new ActionStatus();
        createdStatus.setStatus(ActionStatusEnum.SUCCESS);
        createdStatus.setEntityId(newInstanceId);
        createdStatus.setMessage("New CounterInstance is created with ID " + newInstanceId);

        return Response.created(LinkGenerator.getUriBuilderFromResource(AccountsManagementResource.class, newInstanceId).build())
                .entity(createdStatus).build();
    }
}
