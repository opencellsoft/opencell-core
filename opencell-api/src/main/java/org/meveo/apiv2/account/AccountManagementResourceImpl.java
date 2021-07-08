package org.meveo.apiv2.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AccountManagementResourceImpl  implements AccountManagementResource {
    @Inject
    private AccountManagementService accountManagementService;

    @Override
    public Response changeCustomerAccountParentAccount(String customerAccountCodeOrId, String parentIdOrCode) throws JsonProcessingException {
        Map<String,Object> payload =
                new ObjectMapper().readValue(parentIdOrCode, HashMap.class);
        accountManagementService.changeCustomerAccountParentAccount(customerAccountCodeOrId,
                resolveValue(payload, "parentId"),
                resolveValue(payload, "parentCode"));
        return Response.noContent().build();
    }

    static private String resolveValue(Map<String, Object> payload, String parentId) {
        return Objects.isNull(payload.get(parentId)) ? "" : payload.get(parentId) + "";
    }
}
