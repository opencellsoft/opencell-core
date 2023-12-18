/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.apiv2.rating.impl;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.billing.WalletOperationRerate;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.apiv2.rating.resource.WalletOperationResource;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.service.billing.impl.BatchEntityService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.securityDeposit.impl.FinanceSettingsService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * BatchEntityService : A class for Batch entity persistence services.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
@Interceptors({WsRestApiInterceptor.class})
public class WalletOperationResourceImpl implements WalletOperationResource {

    @Inject
    @MeveoJpa
    private EntityManagerWrapper entityManagerWrapper;

    @Inject
    private FinanceSettingsService financeSettingsService;

    @Inject
    private BatchEntityService batchEntityService;

    @Inject
    private WalletOperationService walletOperationService;

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
            // Use the filter instead the sql to ovoid the implicit cross join generated by JPA (cross join billing_invoice_line)
            Map<String, Object> filter1 = new LinkedHashMap();
            filter1.put("$operator", "OR");
            filter1.put("inList status", List.of(WalletOperationStatusEnum.OPEN, WalletOperationStatusEnum.F_TO_RERATE, WalletOperationStatusEnum.REJECTED));

            Map<String, Object> filter2 = new LinkedHashMap();
            filter2.put("$operator", "AND");
            filter2.put("eq status", WalletOperationStatusEnum.TREATED);
            filter2.put("eq ratedTransaction.status", RatedTransactionStatusEnum.OPEN);
            filter1.put("$filter2", filter2);

            Map<String, Object> filter3 = new LinkedHashMap();
            filter3.put("$operator", "AND");
            filter3.put("eq ratedTransaction.status", RatedTransactionStatusEnum.BILLED);
            filter3.put("eq ratedTransaction.invoiceLine.status", InvoiceLineStatusEnum.OPEN);
            filter1.put("$filter3", filter3);

            filters.put("$filter0101", filter1);

            PaginationConfiguration configuration = new PaginationConfiguration(filters);
            configuration.setFetchFields(Arrays.asList("id"));
            QueryBuilder queryBuilder = walletOperationService.getQuery(configuration);
            List<Long> ids = queryBuilder.getQuery(entityManagerWrapper.getEntityManager()).getResultList();

            StringBuilder updateQuery = new StringBuilder("UPDATE WalletOperation SET ")
                    .append("status=").append(QueryBuilder.paramToString(WalletOperationStatusEnum.TO_RERATE))
                    .append(", updated=").append(QueryBuilder.paramToString(new Date()));

            int updated = batchEntityService.markWoToRerate(updateQuery, ids);

            if (updated > 0) {
                result.setMessage(updated + " Wallet operations updated to status 'TO_RERATE'");
            } else {
                result.setMessage("No Wallet operations found to update");
            }
        }
        return result;
    }
}