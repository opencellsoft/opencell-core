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

import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.PaymentOrRefundEnum;
import org.meveo.service.base.PersistenceService;

@Stateless
public class DDRequestLotOpService extends PersistenceService<DDRequestLotOp> {

	@SuppressWarnings("unchecked")
	public List<DDRequestLotOp> getDDRequestOps(DDRequestBuilder ddRequestBuilder, Seller seller, PaymentOrRefundEnum paymentOrRefundEnum, DDRequestOpEnum ddRequestOpEnum) {
		List<DDRequestLotOp> ddrequestOps = new ArrayList<DDRequestLotOp>();
		if (ddRequestOpEnum == null) {
			ddRequestOpEnum = DDRequestOpEnum.CREATE;
		}
		StringBuilder selectQuery = new StringBuilder("from ").append(DDRequestLotOp.class.getSimpleName())
				.append(" as p  left join fetch p.ddrequestLOT t left join fetch p.seller s where p.status=:statusIN and ")
				.append(" p.ddRequestBuilder=:builderIN and p.paymentOrRefundEnum=:paymentOrRefundEnumIN ").append(seller == null ? "" : " and  p.seller =:sellerIN")
				.append(" and  p.ddrequestOp =:ddrequestOpIN");
		try {
			Query query = getEntityManager().createQuery(selectQuery.toString()).setParameter("statusIN", DDRequestOpStatusEnum.WAIT).setParameter("builderIN", ddRequestBuilder)
					.setParameter("paymentOrRefundEnumIN", paymentOrRefundEnum).setParameter("ddrequestOpIN", ddRequestOpEnum);
			if (seller != null) {
				query = query.setParameter("sellerIN", seller);
			}
			ddrequestOps = (List<DDRequestLotOp>) query.getResultList();
		} catch (Exception e) {
			log.error("failed to get DDRequestOps", e);
		}
		return ddrequestOps;
	}

	/**
	 * 
	 * @param ddRequestLotOpDto
	 * @return
	 */
	public List<DDRequestLotOp> findByParams(DDRequestLotOpDto ddRequestLotOpDto) {
		List<DDRequestLotOp> ddrequestOps = new ArrayList<>();

		StringBuilder selectQuery = new StringBuilder("from ").append(DDRequestLotOp.class.getSimpleName())
				.append(" as ddOp   where ddOp.status=:statusIN and ")
				.append(" ddOp.ddRequestBuilder.code=:builderIN and ").append(" ddOp.ddrequestOp=:ddrequestOpIN and ")
				.append(" ddOp.paymentOrRefundEnum=:paymentOrRefundEnumIN and ")
				.append(" ddOp.fromDueDate =:fromDueDateIN and ").append(" ddOp.toDueDate =:toDueDateIN  ");
		if (!StringUtils.isBlank(ddRequestLotOpDto.getFilterCode())) {
			selectQuery.append(" and ddOp.filter.code =:filterIN  ");
		}
		if (!StringUtils.isBlank(ddRequestLotOpDto.getDueDateRageScriptCode())) {
			selectQuery.append(" and ddOp.scriptInstance.code =:scriptCodeIN  ");
		}
		if (!StringUtils.isBlank(ddRequestLotOpDto.getSellerCode())) {
			selectQuery.append(" and ddOp.seller.code =:sellerIN  ");
		}
		try {
			Query query = getEntityManager().createQuery(selectQuery.toString())
					.setParameter("statusIN", ddRequestLotOpDto.getStatus()).setParameter("ddrequestOpIN", ddRequestLotOpDto.getDdrequestOp())
					.setParameter("builderIN", ddRequestLotOpDto.getDdRequestBuilderCode())
					.setParameter("paymentOrRefundEnumIN", ddRequestLotOpDto.getPaymentOrRefundEnum())
					.setParameter("fromDueDateIN", ddRequestLotOpDto.getFromDueDate())
					.setParameter("toDueDateIN", ddRequestLotOpDto.getToDueDate());
			if (!StringUtils.isBlank(ddRequestLotOpDto.getFilterCode())) {
				query.setParameter("filterIN", ddRequestLotOpDto.getFilterCode());
			}
			if (!StringUtils.isBlank(ddRequestLotOpDto.getDueDateRageScriptCode())) {
				query.setParameter("scriptCodeIN", ddRequestLotOpDto.getDueDateRageScriptCode());
			}
			if (!StringUtils.isBlank(ddRequestLotOpDto.getSellerCode())) {
				query.setParameter("sellerIN", ddRequestLotOpDto.getSellerCode());
			}

			ddrequestOps = (List<DDRequestLotOp>) query.getResultList();
		} catch (Exception e) {
			log.error("failed to findByParams", e);
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
			query.addCriterionDateRangeToTruncatedToDay("o.toDueDate", toDueDate, false, false);
		}

		return query.getQuery(getEntityManager()).getResultList();
	}

}
