package org.meveo.apiv2.rating.impl;

import static org.apache.commons.collections4.ListUtils.partition;
import static org.meveo.commons.utils.ParamBean.getInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.criteria.JoinType;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.billing.WalletOperationRerate;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.apiv2.rating.resource.WalletOperationResource;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.service.billing.impl.BatchEntityService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

@Interceptors({WsRestApiInterceptor.class})
public class WalletOperationResourceImpl implements WalletOperationResource {

    @Inject
    @MeveoJpa
    private EntityManagerWrapper entityManagerWrapper;

    @Inject
    private GenericApiLoadService genericApiLoadService;

    @Inject
    private FinanceSettingsService financeSettingsService;

    @Inject
    private BatchEntityService batchEntityService;

    /**
     *
     */
    public static final int SHORT_MAX_VALUE = 32767;

    @Override
    public ActionStatus markWOToRerate(WalletOperationRerate reRateFilters) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        GenericRequestMapper mapper = new GenericRequestMapper(WalletOperation.class, PersistenceServiceHelper.getPersistenceService());
        Map<String, Object> filters = mapper.evaluateFilters(Objects.requireNonNull(reRateFilters.getFilters()), WalletOperation.class);
        boolean isEntityWithHugeVolume = financeSettingsService.isEntityWithHugeVolume("WalletOperation");


        if (isEntityWithHugeVolume) {
            batchEntityService.create(filters, "MarkWOToRerateJob", "WalletOperation");
            result.setMessage("Wallet operation entity is marked as \"huge\". Your filter is recorded as a marking batch and will be processed later by a marking job. " +
                    "You will receive a notification email when marking is done.");
        } else {
            // Hardcoded filter : as status filter: WO with status in (F_TO_RERATE, OPEN, REJECTED), or status=TREATED and wo.ratedTransaction.status=OPEN
            filters.put("SQL", "(a.status IN ('F_TO_RERATE', 'OPEN', 'REJECTED') OR (a.status = 'TREATED' AND rt.status = 'OPEN'))");

            PaginationConfiguration paginationConfiguration = new PaginationConfiguration("id", PagingAndFiltering.SortOrder.ASCENDING);
            paginationConfiguration.setFilters(filters);
            paginationConfiguration.setFetchFields(new ArrayList<>());
            paginationConfiguration.getFetchFields().add("id");
            // Prepare filter filterQuery
            paginationConfiguration.setJoinType(JoinType.LEFT);

            Map<String, Object> updatedFields = new HashMap<>();
            updatedFields.put("status", WalletOperationStatusEnum.TO_RERATE);
            updatedFields.put("updated", new Date());

            String filterQuery = genericApiLoadService.findAggregatedPaginatedRecordsAsString(WalletOperation.class, " a.ratedTransaction rt ",
                    paginationConfiguration);
            List<Long> ids = entityManagerWrapper.getEntityManager().createQuery(filterQuery).getResultList();

            final int maxValue = getInstance().getPropertyAsInteger("database.number.of.inlist.limit", SHORT_MAX_VALUE);
            List<Integer> updated = new ArrayList<>();
            if (ids.size() > 0) {
                List<List<Long>> listOfSubListIds = partition(ids, maxValue);
                listOfSubListIds.forEach(sublist -> {
                    // Build update filterQuery
                    StringBuilder updateQuery = new StringBuilder("UPDATE WalletOperation a SET");
                    updatedFields.forEach((s, o) ->
                            updateQuery.append(" a.").append(s).append("=").append(QueryBuilder.paramToString(o)).append(",")
                    );
                    updateQuery.setLength(updateQuery.length() - 1);
                    updateQuery.append(" WHERE a.id in (").append(sublist).append(")");

                    updated.add(entityManagerWrapper.getEntityManager().createQuery(updateQuery.toString()).executeUpdate());

                    entityManagerWrapper.getEntityManager().flush();
                    entityManagerWrapper.getEntityManager().clear();
                });
            }

            if (updated.stream().anyMatch(p -> p > 0)) {
                result.setMessage(updated + " Wallet operations updated to status 'TO_RERATE'");
            } else {
                result.setMessage("No Wallet operations found to update");
            }
        }
        return result;
    }
}