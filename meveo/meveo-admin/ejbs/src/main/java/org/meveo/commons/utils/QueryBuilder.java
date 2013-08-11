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
package org.meveo.commons.utils;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.BaseEntity;
import org.meveo.model.crm.Provider;

/**
 * Query builder class for building JPA queries.
 * 
 * <p>
 * Usage example:
 * <p>
 * new QueryBuilder(AClass.class,
 * "a").addCriterionWildcard("a.commercialStatus", commercialStatus,
 * true).addCriterionEnum( "a.billingStatus",
 * billingStatus).addCriterionEnum("a.networkStatus",
 * networkStatus).addCriterionEntity("a.terminalInstance", terminalInstance)
 * .addPaginationConfiguration(configuration);
 * 
 * @author Richard Hallier
 */
public class QueryBuilder {

	private StringBuffer q;
	private Map<String, Object> params;

	private boolean hasOneOrMoreCriteria;
	private boolean inOrClause;
	private int nbCriteriaInOrClause;

	private PaginationConfiguration paginationConfiguration;
	private String paginationSortAlias;

	/**
	 * Constructor.
	 * 
	 * @param sql
	 *            Sql.
	 */
	public QueryBuilder(String sql) {
		q = new StringBuffer(sql);
		params = new HashMap<String, Object>();
		hasOneOrMoreCriteria = false;
		inOrClause = false;
		nbCriteriaInOrClause = 0;
	}

	/**
	 * Constructor.
	 * 
	 * @param qb
	 *            Query builder.
	 */
	public QueryBuilder(QueryBuilder qb) {
		this.q = new StringBuffer(qb.q);
		this.params = new HashMap<String, Object>(qb.params);
		this.hasOneOrMoreCriteria = qb.hasOneOrMoreCriteria;
		this.inOrClause = qb.inOrClause;
		this.nbCriteriaInOrClause = qb.nbCriteriaInOrClause;
	}

	/**
	 * Constructor.
	 * 
	 * @param clazz
	 *            Class for which query is created.
	 * @param alias
	 *            Alias in query.
	 */
	public QueryBuilder(Class<?> clazz, String alias, List<String> fetchFields, Provider provider) {
		this(getInitQuery(clazz, alias, fetchFields));
		if (provider != null && BaseEntity.class.isAssignableFrom(clazz)) {
			addCriterionEntity(alias + ".provider", provider);
		}
	}

	private static String getInitQuery(Class<?> clazz, String alias, List<String> fetchFields) {
		StringBuilder query = new StringBuilder("from " + clazz.getName() + " " + alias);
		if (fetchFields != null && !fetchFields.isEmpty()) {
			for (String fetchField : fetchFields) {
				query.append(" left join fetch " + alias + "." + fetchField);
			}
		}

		return query.toString();
	}

	public StringBuffer getSqlStringBuffer() {
		return q;
	}

	/**
	 * @param paginationConfiguration
	 * @return
	 */
	public QueryBuilder addPaginationConfiguration(PaginationConfiguration paginationConfiguration) {
		return addPaginationConfiguration(paginationConfiguration, null);
	}

	/**
	 * @param paginationConfiguration
	 * @param sortAlias
	 * @return
	 */
	public QueryBuilder addPaginationConfiguration(PaginationConfiguration paginationConfiguration,
			String sortAlias) {
		this.paginationSortAlias = sortAlias;
		this.paginationConfiguration = paginationConfiguration;
		return this;
	}

	/**
	 * @param sql
	 * @return
	 */
	public QueryBuilder addSql(String sql) {
		return addSqlCriterion(sql, null, null);
	}

	/**
	 * @param sql
	 * @param param
	 * @param value
	 * @return
	 */
	public QueryBuilder addSqlCriterion(String sql, String param, Object value) {
		if (param != null && StringUtils.isBlank(value))
			return this;

		if (hasOneOrMoreCriteria) {
			if (inOrClause && nbCriteriaInOrClause != 0)
				q.append(" or ");
			else
				q.append(" and ");
		} else
			q.append(" where ");

		if (inOrClause && nbCriteriaInOrClause == 0)
			q.append("(");

		q.append(sql);

		if (param != null)
			params.put(param, value);

		hasOneOrMoreCriteria = true;
		if (inOrClause)
			nbCriteriaInOrClause++;

		return this;
	}

	/**
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder addBooleanCriterion(String field, Boolean value) {
		if (StringUtils.isBlank(value))
			return this;

		addSql(field + (value.booleanValue() ? " is true " : " is false "));
		return this;
	}

	/**
	 * @param field
	 * @param operator
	 * @param value
	 * @param caseInsensitive
	 * @return
	 */
	public QueryBuilder addCriterion(String field, String operator, Object value,
			boolean caseInsensitive) {
		if (StringUtils.isBlank(value))
			return this;

		StringBuffer sql = new StringBuffer();
		String param = convertFieldToParam(field);
		Object nvalue = value;

		if (caseInsensitive && (value instanceof String))
			sql.append("lower(" + field + ")");
		else
			sql.append(field);

		sql.append(operator + ":" + param);

		if (caseInsensitive && (value instanceof String))
			nvalue = ((String) value).toLowerCase();

		return addSqlCriterion(sql.toString(), param, nvalue);
	}

	/**
	 * @param field
	 * @param entity
	 * @return
	 */
	public QueryBuilder addCriterionEntity(String field, Object entity) {
		if (entity == null)
			return this;

		String param = convertFieldToParam(field);

		return addSqlCriterion(field + "=:" + param, param, entity);
	}

	/**
	 * @param field
	 * @param enumValue
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public QueryBuilder addCriterionEnum(String field, Enum enumValue) {
		if (enumValue == null)
			return this;

		String param = convertFieldToParam(field);

		return addSqlCriterion(field + "=:" + param, param, enumValue);
	}

	/**
	 * Ajouter un critere like
	 * 
	 * @param field
	 * @param value
	 * @param style
	 *            : 0=aucun travail sur la valeur rechercher, 1=Recherche sur
	 *            dbut du mot, 2=Recherche partout dans le mot
	 * @param caseInsensitive
	 * @return
	 */
	public QueryBuilder like(String field, String value, int style, boolean caseInsensitive) {
		if (StringUtils.isBlank(value))
			return this;

		String v = value;

		if (style != 0) {
			if (style == 1 || style == 2)
				v = v + "%";
			if (style == 2)
				v = "%" + v;
		}

		return addCriterion(field, " like ", v, caseInsensitive);
	}

	/**
	 * @param field
	 * @param value
	 * @param caseInsensitive
	 * @return
	 */
	public QueryBuilder addCriterionWildcard(String field, String value, boolean caseInsensitive) {
		if (StringUtils.isBlank(value))
			return this;
		boolean wildcard = (value.indexOf("*") != -1);

		if (wildcard)
			return like(field, value.replace("*", "%"), 0, caseInsensitive);
		else
			return addCriterion(field, "=", value, caseInsensitive);
	}

	/**
	 * add the date field searching support
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder addCriterionDate(String field, Date value) {
		if (StringUtils.isBlank(value))
			return this;
		return addCriterion(field, "=", value, false);

	}

	/**
	 * @param field
	 * @param value
	 * @return
	 */
	public QueryBuilder addCriterionDateTruncatedToDay(String field, Date value) {
		if (StringUtils.isBlank(value))
			return this;
		Calendar c = Calendar.getInstance();
		c.setTime(value);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int date = c.get(Calendar.DATE);
		c.set(year, month, date, 0, 0, 0);
		Date start = c.getTime();
		c.set(year, month, date, 23, 59, 59);
		Date end = c.getTime();

		String startDateParameterName = "start" + field.replace(".", "");
		String endDateParameterName = "end" + field.replace(".", "");
		return addSqlCriterion(field + ">=:" + startDateParameterName, startDateParameterName,
				start).addSqlCriterion(field + "<=:" + endDateParameterName, endDateParameterName,
				end);
	}

	/**
	 * @param field
	 * @param valueFrom
	 * @return
	 */
	public QueryBuilder addCriterionDateRangeFromTruncatedToDay(String field, Date valueFrom) {
		if (StringUtils.isBlank(valueFrom))
			return this;
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(valueFrom);
		int yearFrom = calFrom.get(Calendar.YEAR);
		int monthFrom = calFrom.get(Calendar.MONTH);
		int dateFrom = calFrom.get(Calendar.DATE);
		calFrom.set(yearFrom, monthFrom, dateFrom, 0, 0, 0);
		Date start = calFrom.getTime();

		String startDateParameterName = "start" + field.replace(".", "");
		return addSqlCriterion(field + ">=:" + startDateParameterName, startDateParameterName,
				start);
	}

	/**
	 * @param field
	 * @param valueTo
	 * @return
	 */
	public QueryBuilder addCriterionDateRangeToTruncatedToDay(String field, Date valueTo) {
		if (StringUtils.isBlank(valueTo))
			return this;
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(valueTo);
		int yearTo = calTo.get(Calendar.YEAR);
		int monthTo = calTo.get(Calendar.MONTH);
		int dateTo = calTo.get(Calendar.DATE);
		calTo.set(yearTo, monthTo, dateTo, 23, 59, 59);
		Date end = calTo.getTime();

		String endDateParameterName = "end" + field.replace(".", "");
		return addSqlCriterion(field + "<=:" + endDateParameterName, endDateParameterName, end);
	}

	/**
	 * @param orderColumn
	 * @param ascending
	 */
	public void addOrderCriterion(String orderColumn, boolean ascending) {
		q.append(" ORDER BY " + orderColumn);
		if (ascending) {
			q.append(" ASC ");
		} else {
			q.append(" DESC ");
		}

	}
	
	public void addGroupCriterion(String groupColumn) {
		q.append(" GROUP BY " + groupColumn);
		

	}

	/**
	 * @param orderColumn
	 * @param ascending
	 * @param orderColumn2
	 * @param ascending2
	 * @return
	 */
	public QueryBuilder addOrderDoubleCriterion(String orderColumn, boolean ascending,
			String orderColumn2, boolean ascending2) {
		q.append(" ORDER BY " + orderColumn);
		if (ascending) {
			q.append(" ASC ");
		} else {
			q.append(" DESC ");
		}
		q.append(", " + orderColumn2);
		if (ascending2) {
			q.append(" ASC ");
		} else {
			q.append(" DESC ");
		}
		return this;
	}

	/**
	 * @param orderColumn
	 * @param ascending
	 * @return
	 */
	public QueryBuilder addOrderUniqueCriterion(String orderColumn, boolean ascending) {
		q.append(" ORDER BY " + orderColumn);
		if (ascending) {
			q.append(" ASC ");
		} else {
			q.append(" DESC ");
		}
		return this;
	}

	/**
	 * @return
	 */
	public QueryBuilder startOrClause() {
		inOrClause = true;
		nbCriteriaInOrClause = 0;
		return this;
	}

	/**
	 * @return
	 */
	public QueryBuilder endOrClause() {
		if (nbCriteriaInOrClause != 0)
			q.append(")");

		inOrClause = false;
		nbCriteriaInOrClause = 0;
		return this;
	}

	/**
	 * @param em
	 * @return
	 */
	public Query getQuery(EntityManager em) {
		applyPagination(paginationSortAlias);

		Query result = em.createQuery(q.toString());
		applyPagination(result);

		for (Map.Entry<String, Object> e : params.entrySet())
			result.setParameter(e.getKey(), e.getValue());
		return result;
	}

	/**
	 * @param em
	 * @return
	 */
	public Query getCountQuery(EntityManager em) {
		String from = "from ";
		String s = "select count(*) " + q.toString().substring(q.indexOf(from));

		Query result = em.createQuery(s);
		for (Map.Entry<String, Object> e : params.entrySet())
			result.setParameter(e.getKey(), e.getValue());
		return result;
	}

	/**
	 * @param em
	 * @return
	 */
	public Long count(EntityManager em) {
		Query query = getCountQuery(em);
		return (Long) query.getSingleResult();
	}

	/**
	 * @param em
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List find(EntityManager em) {
		Query query = getQuery(em);
		return query.getResultList();
	}

	/**
	 * @param field
	 * @return
	 */
	private String convertFieldToParam(String field) {
		field = field.replace(".", "_").replace("(", "_").replace(")", "_");
		StringBuilder newField = new StringBuilder(field);
		while (params.containsKey(newField.toString()))
			newField = new StringBuilder(field).append("_"
					+ String.valueOf(new Random().nextInt(100)));
		return newField.toString();
	}

	/**
	 * @param alias
	 */
	private void applyPagination(String alias) {
		if (paginationConfiguration == null)
			return;

		if (paginationConfiguration.isSorted())
			addOrderCriterion(
					((alias != null) ? (alias + ".") : "") + paginationConfiguration.getSortField(),
					paginationConfiguration.isAscendingSorting());

	}

	/**
	 * @param query
	 */
	private void applyPagination(Query query) {
		if (paginationConfiguration == null)
			return;

		query.setFirstResult(paginationConfiguration.getFirstRow());
		query.setMaxResults(paginationConfiguration.getNumberOfRows());
	}

	/* DEBUG */
	public void debug() {
		System.out.println("Requete : " + q.toString());
		for (Map.Entry<String, Object> e : params.entrySet())
			System.out.println("Param name:" + e.getKey() + " value:" + e.getValue().toString());
	}

	public String getSqlString() {
		return q.toString();
	}

	public Map<String, Object> getParams() {
		return Collections.unmodifiableMap(params);
	}

	public QueryBuilder(Class<?> clazz, String alias) {
		this("from " + clazz.getName() + " " + alias);
	}

	public String toString() {
		String result = q.toString();
		for (Map.Entry<String, Object> e : params.entrySet()) {
			result = result + " Param name:" + e.getKey() + " value:" + e.getValue().toString();
		}
		return result;
	}
}
