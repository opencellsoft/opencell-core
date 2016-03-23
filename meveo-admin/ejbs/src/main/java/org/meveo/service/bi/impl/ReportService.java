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
package org.meveo.service.bi.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import org.meveo.model.bi.Report;
import org.meveo.service.base.PersistenceService;

/**
 * Report service implementation.
 * 
 */
@Stateless
public class ReportService extends PersistenceService<Report> {

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getRows(String queryString) {
		Query query = getEntityManager().createNativeQuery(queryString);
		return query.getResultList();
	}

	public List<Object> getBordereauRemiseChequeRecords(Date startDate,
			Date endDate) {

		// Query query = em
		// .createQuery(
		// "select a.accountingCode, a.occDescription, sum(amount) as amount, a.occCode from "
		// + getEntityClass().getSimpleName()
		// +
		// " a where a.category = 0 and  a.transactionDate>:startDate and a.transactionDate <= :endDate  group by a.occCode, a.accountingCode, a.occDescription")
		// .setParameter("startDate", startDate).setParameter("endDate",
		// endDate);
		//
		// return query.getResultList();
		return null;

	}

}
