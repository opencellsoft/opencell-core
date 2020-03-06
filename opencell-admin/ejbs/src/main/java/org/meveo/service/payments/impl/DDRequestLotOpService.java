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
package org.meveo.service.payments.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class DDRequestLotOpService extends PersistenceService<DDRequestLotOp> {

    @SuppressWarnings("unchecked")
    public List<DDRequestLotOp> getDDRequestOps(DDRequestBuilder ddRequestBuilder, Seller seller,PaymentOrRefundEnum paymentOrRefundEnum) {
        List<DDRequestLotOp> ddrequestOps = new ArrayList<DDRequestLotOp>();

        StringBuilder selectQuery = new StringBuilder("from ")
                .append(DDRequestLotOp.class.getSimpleName())
                .append(" as p  left join fetch p.ddrequestLOT t where p.status=:statusIN and ")
                .append("p.ddRequestBuilder=:builderIN and p.paymentOrRefundEnum=:paymentOrRefundEnumIN ")
                .append(seller == null ? "" : " and  p.seller =:sellerIN");
        try {
            Query query = getEntityManager()
                .createQuery(selectQuery.toString())
                .setParameter("statusIN", DDRequestOpStatusEnum.WAIT).setParameter("builderIN", ddRequestBuilder)
                .setParameter("paymentOrRefundEnumIN", paymentOrRefundEnum);
            if (seller != null) {
                query = query.setParameter("sellerIN", seller);
            }

            ddrequestOps = (List<DDRequestLotOp>) query.getResultList();
        } catch (Exception e) {
            log.error("failed to get DDRequestOps", e);
        }
        return ddrequestOps;
    }

    @SuppressWarnings("unchecked")
    public List<DDRequestLotOp> findByDateStatus(Date fromDueDate, Date toDueDate, DDRequestOpStatusEnum status) {
        QueryBuilder query = new QueryBuilder(DDRequestLotOp.class, "o");

        if (!StringUtils.isBlank(status)) {
            query.addCriterionEnum("o.status", status);
        }
        if (!StringUtils.isBlank(fromDueDate)) {
            query.addCriterionDateRangeFromTruncatedToDay("o.fromDueDate", fromDueDate);
        }
        if (!StringUtils.isBlank(toDueDate)) {
            query.addCriterionDateRangeToTruncatedToDay("o.toDueDate", toDueDate);
        }

        return query.getQuery(getEntityManager()).getResultList();
    }

}
