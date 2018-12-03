/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.payments.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.service.base.PersistenceService;

@Stateless
public class DDRequestLotOpService extends PersistenceService<DDRequestLotOp> {

    @SuppressWarnings("unchecked")
    public List<DDRequestLotOp> getDDRequestOps(DDRequestBuilder ddRequestBuilder, PaymentGateway paymentGateway) {
        List<DDRequestLotOp> ddrequestOps = new ArrayList<DDRequestLotOp>();

        try {
            Query query = getEntityManager()
                .createQuery("from " + DDRequestLotOp.class.getSimpleName() + " as p  left join fetch p.ddrequestLOT t where p.status=:status and "
                        + "p.ddRequestBuilder=:builderIN " + (paymentGateway == null ? "" : " and  p.paymentGateway =:paymentGatewayIN"))
                .setParameter("status", DDRequestOpStatusEnum.WAIT).setParameter("builderIN", ddRequestBuilder);
            if (paymentGateway != null) {
                query = query.setParameter("paymentGatewayIN", paymentGateway);
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
