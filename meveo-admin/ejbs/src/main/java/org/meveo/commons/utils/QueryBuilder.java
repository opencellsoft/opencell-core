/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.commons.utils;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
	
    public enum QueryLikeStyleEnum {
        MATCH_EQUAL, MATCH_BEGINNING, MATCH_ANYWHERE
    }

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
	public QueryBuilder(Class<?> clazz, String alias, List<String> fetchFields,
			Provider provider) {
		this(getInitQuery(clazz, alias, fetchFields));
		if (provider != null && BaseEntity.class.isAssignableFrom(clazz)) {
			addCriterionEntity(alias + ".provider", provider);
		}
	}

	private static String getInitQuery(Class<?> clazz, String alias,
			List<String> fetchFields) {
		StringBuilder query = new StringBuilder("from " + clazz.getName() + " "
				+ alias);
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
	public QueryBuilder addPaginationConfiguration(
			PaginationConfiguration paginationConfiguration) {
		return addPaginationConfiguration(paginationConfiguration, null);
	}

	/**
	 * @param paginationConfiguration
	 * @param sortAlias
	 * @return
	 */
	public QueryBuilder addPaginationConfiguration(
			PaginationConfiguration paginationConfiguration, String sortAlias) {
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
	public QueryBuilder addCriterion(String field, String operator,
			Object value, boolean caseInsensitive) {
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
        return addCriterionEntity(field, entity, " = ");
    }

	/**
	 * @param field
	 * @param entity
     * @param condition Comparison type
	 * @return
	 */
	public QueryBuilder addCriterionEntity(String field, Object entity, String condition) {
		if (entity == null)
			return this;

		String param = convertFieldToParam(field);

		return addSqlCriterion(field + condition+":" + param, param, entity);
	}

	
	/**
     * @param field 
     * @param enumValue
     * @return
     */
	public QueryBuilder addCriterionEnum(String field, Enum enumValue){
	    return addCriterionEnum(field, enumValue, "=");
	}
	
	/**
	 * @param field
	 * @param enumValue
	 * @param condition Comparison type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public QueryBuilder addCriterionEnum(String field, Enum enumValue, String condition) {
		if (enumValue == null)
			return this;

		String param = convertFieldToParam(field);

        return addSqlCriterion(field + " " + condition + ":" + param, param, enumValue);
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
    public QueryBuilder like(String field, String value, QueryLikeStyleEnum style, boolean caseInsensitive){
        return like(field, value, style, caseInsensitive, false);
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
     * @param addNot Should NOT be added to comparison
	 * @return
	 */
    public QueryBuilder like(String field, String value, QueryLikeStyleEnum style, boolean caseInsensitive, boolean addNot) {
		if (StringUtils.isBlank(value)) {
			return this;
		}

		String v = value;

        if (style == QueryLikeStyleEnum.MATCH_BEGINNING || style == QueryLikeStyleEnum.MATCH_ANYWHERE) {
            v = v + "%";
        }
        if (style == QueryLikeStyleEnum.MATCH_ANYWHERE) {
            v = "%" + v;
        }

		return addCriterion(field, addNot? "not like ": " like ", v, caseInsensitive);
	}
	
	   /**
     * @param field
     * @param value
     * @param caseInsensitive
     * @return
     */
    public QueryBuilder addCriterionWildcard(String field, String value, boolean caseInsensitive){
        return addCriterionWildcard(field, value, caseInsensitive, false);
    }

	/**
	 * @param field
	 * @param value
	 * @param caseInsensitive
     * @param addNot Should NOT be added to comparison
	 * @return
	 */
    public QueryBuilder addCriterionWildcard(String field, String value, boolean caseInsensitive, boolean addNot) {
	    
		if (StringUtils.isBlank(value)){
			return this;
		}
		boolean wildcard = (value.indexOf("*") != -1);

        if (wildcard) {
            return like(field, value.replace("*", "%"), QueryLikeStyleEnum.MATCH_EQUAL, caseInsensitive, addNot);
        } else {
            return addCriterion(field, addNot? " != ":" = ", value, caseInsensitive);
        }
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
		return addSqlCriterion(field + ">=:" + startDateParameterName,
				startDateParameterName, start)
				.addSqlCriterion(field + "<=:" + endDateParameterName,
						endDateParameterName, end);
	}

	/**
	 * @param field
	 * @param valueFrom
	 * @return
	 */
	public QueryBuilder addCriterionDateRangeFromTruncatedToDay(String field,
			Date valueFrom) {
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
		return addSqlCriterion(field + ">=:" + startDateParameterName,
				startDateParameterName, start);
	}

	/**
	 * @param field
	 * @param valueTo
	 * @return
	 */
	public QueryBuilder addCriterionDateRangeToTruncatedToDay(String field,
			Date valueTo) {
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
		return addSqlCriterion(field + "<=:" + endDateParameterName,
				endDateParameterName, end);
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
	public QueryBuilder addOrderDoubleCriterion(String orderColumn,
			boolean ascending, String orderColumn2, boolean ascending2) {
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
	public QueryBuilder addOrderUniqueCriterion(String orderColumn,
			boolean ascending) {
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
			addOrderCriterion(((alias != null) ? (alias + ".") : "")
					+ paginationConfiguration.getSortField(),
					paginationConfiguration.isAscendingSorting());

	}

	/**
	 * @param query
	 */
	private void applyPagination(Query query) {
		if (paginationConfiguration == null) {
			return;
		}

        if (paginationConfiguration.getFirstRow() != null) {
            query.setFirstResult(paginationConfiguration.getFirstRow());
        }
        if (paginationConfiguration.getNumberOfRows() != null) {
            query.setMaxResults(paginationConfiguration.getNumberOfRows());
        }
	}

	/* DEBUG */
	public void debug() {
		System.out.println("Requete : " + q.toString());
		for (Map.Entry<String, Object> e : params.entrySet())
			System.out.println("Param name:" + e.getKey() + " value:"
					+ e.getValue().toString());
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
			result = result + " Param name:" + e.getKey() + " value:"
					+ e.getValue().toString();
		}
		return result;
	}
}
