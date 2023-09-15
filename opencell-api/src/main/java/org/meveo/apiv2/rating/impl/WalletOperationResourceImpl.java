package org.meveo.apiv2.rating.impl;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.billing.WalletOperationRerate;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.GenericApiAlteringService;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.apiv2.rating.resource.WalletOperationResource;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;

import javax.inject.Inject;
import javax.interceptor.Interceptors;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Interceptors({ WsRestApiInterceptor.class })
public class WalletOperationResourceImpl implements WalletOperationResource {

    @Inject
    private GenericApiAlteringService genericAlteringService;

    @Override
    public ActionStatus markWOToRerate(WalletOperationRerate reRateFilters) {
        GenericRequestMapper mapper = new GenericRequestMapper(WalletOperation.class, PersistenceServiceHelper.getPersistenceService());
        Map<String, Object> filters = mapper.evaluateFilters(Objects.requireNonNull(reRateFilters.getFilters()), WalletOperation.class);

        // Hardcoded filter : as status filter: WO with status in (F_TO_RERATE, OPEN, REJECTED), or status=TREATED and wo.ratedTransaction.status=OPEN
        filters.put("SQL", "(a.status IN ('F_TO_RERATE', 'OPEN', 'REJECTED') OR (a.status = 'TREATED' AND rt.status = 'OPEN'))");

        PaginationConfiguration paginationConfiguration = new PaginationConfiguration("id", PagingAndFiltering.SortOrder.ASCENDING);
        paginationConfiguration.setFilters(filters);
        paginationConfiguration.setFetchFields(new ArrayList<>());
        paginationConfiguration.getFetchFields().add("id");

        Map<String, Object> toUpdateFields = new HashMap<>();
        toUpdateFields.put("status", WalletOperationStatusEnum.TO_RERATE);
        toUpdateFields.put("updated", new Date());

        int updated = genericAlteringService.massUpdate(WalletOperation.class.getSimpleName(),
                WalletOperation.class.getSimpleName(),
                toUpdateFields, filters);

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        if (updated > 0) {
            result.setMessage(updated + " Wallet operations updated to status 'TO_RERATE'");
        } else {
            result.setMessage("No Wallet operations found to update");
        }

        return result;
    }
}