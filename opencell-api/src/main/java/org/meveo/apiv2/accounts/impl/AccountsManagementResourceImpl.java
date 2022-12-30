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

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

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
        List<Long> newInstanceIds = accountsManagementApiService.createCounterInstance(dto);
        ActionStatus createdStatus = new ActionStatus();
        createdStatus.setStatus(ActionStatusEnum.SUCCESS);
        createdStatus.setMessage(newInstanceIds.size() + " new CounterInstance is created  " + newInstanceIds);

        return Response.created(LinkGenerator.getUriBuilderFromResource(AccountsManagementResource.class, newInstanceIds.toString()).build())
                .entity(createdStatus).build();
    }

    @Override
    public Response updateCounterInstance(Long id, CounterInstanceDto dto) {
        List<Long> updatedInstanceIds = accountsManagementApiService.updateCounterInstance(id, dto);
        ActionStatus createdStatus = new ActionStatus();
        createdStatus.setStatus(ActionStatusEnum.SUCCESS);
        createdStatus.setMessage(updatedInstanceIds.size() + " updated CounterInstance " + updatedInstanceIds);

        return Response.created(LinkGenerator.getUriBuilderFromResource(AccountsManagementResource.class, updatedInstanceIds.toString()).build())
                .entity(createdStatus).build();
    }
}
