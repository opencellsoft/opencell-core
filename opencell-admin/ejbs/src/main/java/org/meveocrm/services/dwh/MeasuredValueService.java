package org.meveocrm.services.dwh;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.model.dwh.MeasuredValue;
import org.meveo.model.dwh.MeasurementPeriodEnum;
import org.meveo.service.base.PersistenceService;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.1
 */
@Stateless
public class MeasuredValueService extends PersistenceService<MeasuredValue> {

    /**
     * @param date
     * @param period
     * @param mq
     * @return MeasuredValue
     */
    public MeasuredValue getByDate(Date date, MeasurementPeriodEnum period, MeasurableQuantity mq) {
        return getByDate(getEntityManager(), date, period, mq);
    }

    /**
     * @param em
     * @param date
     * @param period
     * @param mq
     * @return MeasuredValue
     */
    public MeasuredValue getByDate(EntityManager em, Date date, MeasurementPeriodEnum period, MeasurableQuantity mq) {
        MeasuredValue result = null;
        // QueryBuilder queryBuilder = new QueryBuilder(MeasuredValue.class, " m ");
        // queryBuilder.addCriterionDate("m.date", date);
        // queryBuilder.addCriterionEnum("m.measurementPeriod", period);
        // queryBuilder.addCriterionEntity("m.measurableQuantity", mq);
        // Query query = queryBuilder.getQuery(em);
        //
        // log.info("> MeasuredValueService > getByDate > query 1 > {}", query.toString());

        if (date == null || period == null || mq == null) {
            return null;
        }

        Query myQuery = getEntityManager()
            .createQuery("from " + MeasuredValue.class.getName() + " m where m.date=:date and m.measurementPeriod=:period and m.measurableQuantity= :measurableQuantity");
        myQuery.setParameter("date", date).setParameter("period", period).setParameter("measurableQuantity", mq);

        @SuppressWarnings("unchecked")
        List<MeasuredValue> res = myQuery.getResultList();
        if (res.size() > 0) {
            result = res.get(0);
        }
        return result;
    }

    /**
     * 
     * @param dimensionIndex
     * @param fromDate
     * @param toDate
     * @param mq
     * @return list
     */
    @SuppressWarnings("rawtypes")
    public List<String> getDimensionList(int dimensionIndex, Date fromDate, Date toDate, MeasurableQuantity mq) {
        List<String> result = new ArrayList<String>();
        Calendar end = Calendar.getInstance();
        // result.add("");
        String dimension = "dimension" + dimensionIndex;
        String sqlQuery = "SELECT DISTINCT(mv." + dimension + ") FROM " + MeasuredValue.class.getName() + " mv WHERE mv.measurableQuantity=" + mq.getId() + " ";
        if (fromDate != null) {
            Calendar start = Calendar.getInstance();
            start.setTime(fromDate);
            start.set(Calendar.HOUR, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);
            sqlQuery += " AND (mv.date >= '" + start.getTime() + "')";
        }
        if (toDate != null) {
            end.setTime(toDate);
            end.set(Calendar.HOUR, 0);
            end.set(Calendar.MINUTE, 0);
            end.set(Calendar.SECOND, 0);
            end.set(Calendar.MILLISECOND, 0);
            sqlQuery += " AND (mv.date < '" + end.getTime() + "')";
        }
        sqlQuery += " AND mv.measurementPeriod = '" + mq.getMeasurementPeriod() + "' ";
        sqlQuery += " ORDER BY mv." + dimension + " ASC";
        Query query = getEntityManager().createQuery(sqlQuery);
        List resultList = query.getResultList();
        if (resultList != null) {
            for (Object res : resultList) {
                if (res != null) {
                    result.add(res.toString());
                }
            }
        }
        return result;
    }

    /**
     * List of measured values.
     * 
     * @param code
     * @param fromDate
     * @param toDate
     * @param period
     * @param mq
     * @return list of measured values
     */
    public List<MeasuredValue> getByDateAndPeriod(String code, Date fromDate, Date toDate, MeasurementPeriodEnum period, MeasurableQuantity mq) {
        return getByDateAndPeriod(code, fromDate, toDate, period, mq, false);
    }

    /**
     * @param code
     * @param fromDate
     * @param toDate
     * @param period
     * @param mq
     * @param sortByDate
     * @return list of measured values
     */
    @SuppressWarnings("unchecked")
    public List<MeasuredValue> getByDateAndPeriod(String code, Date fromDate, Date toDate, MeasurementPeriodEnum period, MeasurableQuantity mq, Boolean sortByDate) {
        String sqlQuery = "";

        boolean whereExists = false;
        if (code != null) {
            sqlQuery += "m.code = :code ";
            whereExists = true;
        }

        if (fromDate != null) {
            if (!whereExists) {
                sqlQuery += "m.date >= :fromDate ";
                whereExists = true;
            } else {
                sqlQuery += "and m.date >= :fromDate ";
            }
        }
        if (toDate != null) {
            if (!whereExists) {
                sqlQuery += "m.date >= :toDate ";
                whereExists = true;
            } else {
                sqlQuery += "and m.date >= :toDate ";
            }
        }

        if (period != null) {
            if (!whereExists) {
                sqlQuery += "m.measurementPeriod = :period ";
                whereExists = true;
            } else {
                sqlQuery += "and m.measurementPeriod = :period ";
            }
        }
        if (mq != null) {
            if (!whereExists) {
                sqlQuery += "m.measurableQuantity.id = :id ";
                whereExists = true;
            } else {
                sqlQuery += "and m.measurableQuantity.id = :id ";
            }
        }

        if (sortByDate) {
            sqlQuery += "ORDER BY m.date ASC";
        }

        Query myQuery;
        if (whereExists) {
            sqlQuery = "FROM " + MeasuredValue.class.getName() + " m WHERE " + sqlQuery;
            myQuery = getEntityManager().createQuery(sqlQuery);
            if (code != null) {
                myQuery.setParameter("code", code.toUpperCase());
            }
            if (fromDate != null) {
                myQuery.setParameter("fromDate", fromDate);
            }
            if (toDate != null) {
                myQuery.setParameter("toDate", toDate);

            }
            if (period != null) {
                myQuery.setParameter("period", period);
            }
            if (mq != null) {
                myQuery.setParameter("id", mq.getId());
            }
            if (sortByDate) {
                sqlQuery += "ORDER BY m.date ASC";
            }
        } else {
            sqlQuery = "FROM " + MeasuredValue.class.getName() + " m " + sqlQuery;
            myQuery = getEntityManager().createQuery(sqlQuery);
        }

        return myQuery.getResultList();
    }
}
