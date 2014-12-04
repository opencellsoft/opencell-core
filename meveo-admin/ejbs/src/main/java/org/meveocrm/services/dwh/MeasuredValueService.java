package org.meveocrm.services.dwh;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.service.base.PersistenceService;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.model.dwh.MeasurementPeriodEnum;

@Stateless
public class MeasuredValueService extends PersistenceService<MeasuredValue> {

	@SuppressWarnings("unchecked")
	public List<MeasuredValue> getMeasuredValueByDateRange(Date startDate,
			Date endDate) {

		if (startDate != null && endDate != null) {
			QueryBuilder qb = new QueryBuilder("FROM "
					+ MeasuredValue.class.getName());
			//qb.startInnerAndClause();
			qb.addCriterionDateRangeFromTruncatedToDay("date", startDate);
			qb.addCriterionDateRangeToTruncatedToDay("date", endDate);
			//qb.endInnerAndClause();

			Query query = qb.getQuery(getEntityManager());

			List<MeasuredValue> mvList = (List<MeasuredValue>) query
					.getResultList();
			if (mvList.size() > 0) {
				return mvList;
			}
		}
		return null;

	}

	
	public MeasuredValue getByDate(EntityManager em,Date date, MeasurementPeriodEnum period,
			MeasurableQuantity mq){

		QueryBuilder queryBuilder = new QueryBuilder("FROM "
				+ MeasuredValue.class.getName() + " m ");
		queryBuilder.addCriterionDate("m.date", date);
		queryBuilder
					.addCriterion("m.measurementPeriod", "=", period, false);
		queryBuilder.addCriterion("m.measurableQuantity", "=", mq, false);
		Query query = queryBuilder.getQuery(em);
		return (MeasuredValue) query.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<MeasuredValue> getByDateAndPeriod(String code,
			Date fromDate, Date toDate, MeasurementPeriodEnum period,
			MeasurableQuantity mq) {

		QueryBuilder queryBuilder = new QueryBuilder("FROM "
				+ MeasuredValue.class.getName() + " m ");

		if (code != null) {
				queryBuilder.addSql(" m.measurableQuantity.code = '"
						+ code.toUpperCase() + "' ");
		}
		
		if (fromDate != null && toDate == null) {
			Calendar start = Calendar.getInstance();
			start.setTime(fromDate);
			start.set(Calendar.HOUR, 0);
			start.set(Calendar.MINUTE, 0);
			start.set(Calendar.SECOND, 0);
			start.set(Calendar.MILLISECOND, 0);
			Calendar end = Calendar.getInstance();
			end.setTime(start.getTime());
			end.add(Calendar.DAY_OF_MONTH, 1);

			queryBuilder.addCriterion("m.date", ">=", start.getTime(), false);
			queryBuilder.addCriterion("m.date", "<", end.getTime(), false);
		} else if (fromDate != null && toDate != null) {

			queryBuilder.addCriterion("m.date", ">=", fromDate, false);
			queryBuilder.addCriterion("m.date", "<", toDate, false);
		}
		if (period != null) {
			queryBuilder
					.addCriterion("m.measurementPeriod", "=", period, false);
		}
		if (mq != null) {
			queryBuilder.addCriterion("m.measurableQuantity", "=", mq, false);
		}

		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	public Long getMeasuredValueSumByDate(Date fromDate, Date toDate,
			String code, MeasurementPeriodEnum period,
			String followUpTheme, Boolean includeLastdDay) {
		Calendar cal = Calendar.getInstance();
		String sqlQuery = null;
		if (fromDate == null && toDate != null) {
			if (!includeLastdDay) {
				cal.setTime(toDate);
				sqlQuery = "SELECT SUM(mv.value) FROM "
						+ MeasuredValue.class.getName()
						+ " mv WHERE (mv.date > '" + cal.getTime().toString()
						+ "')";
			} else {
				cal.setTime(toDate);
				sqlQuery = "SELECT SUM(mv.value) FROM "
						+ MeasuredValue.class.getName()
						+ " mv WHERE (mv.date >= '" + cal.getTime().toString()
						+ "')";
			}

		} else if (fromDate != null && toDate == null) {
			cal.setTime(fromDate);
			cal.add(Calendar.DAY_OF_MONTH, 1);

			sqlQuery = "SELECT SUM(mv.value) FROM "
					+ MeasuredValue.class.getName() + " mv WHERE (mv.date >= '"
					+ fromDate.toString() + "' AND mv.date < '"
					+ cal.getTime().toString() + "')";

		} else if (fromDate != null && toDate != null) {
			cal.setTime(toDate);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			sqlQuery = "SELECT SUM(mv.value) FROM "
					+ MeasuredValue.class.getName() + " mv WHERE (mv.date >= '"
					+ fromDate.toString() + "' AND mv.date < '"
					+ cal.getTime().toString() + "') ";

		}

		if (period != null) {
			sqlQuery += " AND mv.measurementPeriod = '" + period.name() + "' ";
		}
		if (code != null) {
				sqlQuery += " AND mv.measurableQuantity.code ='"
						+ code.toUpperCase() + "'";
		}
		long time = System.currentTimeMillis();
		Query query = getEntityManager().createQuery(sqlQuery);

		Long result = 0L;
		if ((Long) query.getSingleResult() != null) {
			result = (Long) query.getSingleResult();
		}
		time = System.currentTimeMillis() - time;
		return result;

	}

	public Long getMeasuredValueSumByDate(EntityManager em, Date fromDate,
			Date toDate, String code, MeasurementPeriodEnum period,
			String followUpTheme, Boolean includeLastdDay) {
		Calendar cal = Calendar.getInstance();
		String sqlQuery = null;
		if (fromDate == null && toDate != null) {
			if (!includeLastdDay) {
				cal.setTime(toDate);
				sqlQuery = "SELECT SUM(mv.value) FROM "
						+ MeasuredValue.class.getName()
						+ " mv WHERE (mv.date < '" + cal.getTime().toString()
						+ "')";
			} else {
				cal.setTime(toDate);
				sqlQuery = "SELECT SUM(mv.value) FROM "
						+ MeasuredValue.class.getName()
						+ " mv WHERE (mv.date <= '" + cal.getTime().toString()
						+ "')";
			}

		} else if (fromDate != null && toDate == null) {
			cal.setTime(fromDate);
			cal.add(Calendar.DAY_OF_MONTH, 1);

			sqlQuery = "SELECT SUM(mv.value) FROM "
					+ MeasuredValue.class.getName() + " mv WHERE (mv.date >= '"
					+ fromDate.toString() + "' AND mv.date < '"
					+ cal.getTime().toString() + "')";

		} else if (fromDate != null && toDate != null) {
			cal.setTime(toDate);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			sqlQuery = "SELECT SUM(mv.value) FROM "
					+ MeasuredValue.class.getName() + " mv WHERE (mv.date >= '"
					+ fromDate.toString() + "' AND mv.date < '"
					+ cal.getTime().toString() + "') ";

		}

		if (period != null) {
			sqlQuery += " AND mv.measurementPeriod = '" + period.name() + "' ";
		}
		if (code != null) {
			sqlQuery += " AND mv.measurableQuantity.code ='"
						+ code.toUpperCase() + "'";
			
		}

		long time = System.currentTimeMillis();
		Query query = em.createQuery(sqlQuery);

		Long result = 0L;
		if ((Long) query.getSingleResult() != null) {
			result = (Long) query.getSingleResult();
		}
		time = System.currentTimeMillis() - time;
		log.debug("Time : "+time+ " SQL : "+sqlQuery);
		return result;

	}
}
