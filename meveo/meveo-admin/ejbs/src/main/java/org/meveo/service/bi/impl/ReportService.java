/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.service.bi.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;
import javax.persistence.Query;

import org.meveo.model.bi.Report;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.bi.local.ReportServiceLocal;

/**
 * Report service implementation.
 * 
 */
@Stateless
@Named
public class ReportService extends PersistenceService<Report> implements
		ReportServiceLocal {

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Object> getRows(String queryString) {
		Query query = dwhEntityManager.createNativeQuery(queryString);
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
