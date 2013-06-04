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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.BaseEntity;
import org.meveo.model.IdentifiableEnum;
import org.meveo.model.UniqueEntity;
import org.meveo.model.bi.JobExecutionHisto;
import org.meveo.service.base.PersistenceService;

/**
 * Job history service implementation.
 */
@Stateless
@LocalBean
public class JobExecutionHistoryService extends PersistenceService<JobExecutionHisto> {
	@Override
	public long count(PaginationConfiguration config) {

		QueryBuilder queryBuilder = getQuery(config);
		return queryBuilder.count(getEntityManager());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#list(org.meveo.admin.util.pagination.PaginationConfiguration)
	 */
	@Override
	@SuppressWarnings({ "unchecked" })
	public List<JobExecutionHisto> list(PaginationConfiguration config) {
		QueryBuilder queryBuilder = getQuery(config);
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	/**
	 * Overided getQuery method, because we do not need to select data according
	 * to current Provider
	 * 
	 * @see org.meveo.service.base.PersistenceService#getQuery(org.meveo.admin.util.pagination.PaginationConfiguration)
	 */
	@SuppressWarnings("rawtypes")
	private QueryBuilder getQuery(PaginationConfiguration config) {

		final Class<? extends JobExecutionHisto> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", config.getFetchFields(),
				null);
		Map<String, Object> filters = config.getFilters();
		if (filters != null) {
			if (!filters.isEmpty()) {
				for (String key : filters.keySet()) {
					Object filter = filters.get(key);
					if (filter != null) {
						// if ranged search (from - to fields)
						if (key.contains("fromRange-")) {
							String parsedKey = key.substring(10);
							if (filter instanceof Double) {
								BigDecimal rationalNumber = new BigDecimal((Double) filter);
								queryBuilder.addCriterion("a." + parsedKey, " >= ", rationalNumber,
										true);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + parsedKey, " >= ", filter, true);
							} else if (filter instanceof Date) {
								queryBuilder.addCriterionDateRangeFromTruncatedToDay("a."
										+ parsedKey, (Date) filter);
							}
						} else if (key.contains("toRange-")) {
							String parsedKey = key.substring(8);
							if (filter instanceof Double) {
								BigDecimal rationalNumber = new BigDecimal((Double) filter);
								queryBuilder.addCriterion("a." + parsedKey, " <= ", rationalNumber,
										true);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + parsedKey, " <= ", filter, true);
							} else if (filter instanceof Date) {
								queryBuilder.addCriterionDateRangeToTruncatedToDay(
										"a." + parsedKey, (Date) filter);
							}
						} else if (key.contains("list-")) {
							// if searching elements from list
							String parsedKey = key.substring(5);
							queryBuilder.addSqlCriterion(":" + parsedKey + " in elements(a."
									+ parsedKey + ")", parsedKey, filter);
						}
						// if not ranged search
						else {
							if (filter instanceof String) {
								// if contains dot, that means join is needed
								String filterString = (String) filter;
								queryBuilder.addCriterionWildcard("a." + key, filterString, true);
							} else if (filter instanceof Date) {
								queryBuilder.addCriterionDateTruncatedToDay("a." + key,
										(Date) filter);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + key, " = ", filter, true);
							} else if (filter instanceof Boolean) {
								queryBuilder.addCriterion("a." + key, " is ", filter, true);
							} else if (filter instanceof Enum) {
								if (filter instanceof IdentifiableEnum) {
									String enumIdKey = new StringBuilder(key).append("Id")
											.toString();
									queryBuilder.addCriterion("a." + enumIdKey, " = ",
											((IdentifiableEnum) filter).getId(), true);
								} else {
									queryBuilder.addCriterionEnum("a." + key, (Enum) filter);
								}
							} else if (BaseEntity.class.isAssignableFrom(filter.getClass())) {
								queryBuilder.addCriterionEntity("a." + key, filter);
							} else if (filter instanceof UniqueEntity) {
								queryBuilder.addCriterionEntity("a." + key, filter);
							}
						}
					}
				}
			}
		}
		queryBuilder.addPaginationConfiguration(config, "a");
		return queryBuilder;
	}
}
