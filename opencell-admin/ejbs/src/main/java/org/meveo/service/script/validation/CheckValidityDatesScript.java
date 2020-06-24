package org.meveo.service.script.validation;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.joda.time.DateTimeComparator;
import org.meveo.admin.exception.ValidationException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.CustomTableEvent;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.custom.CustomTableService;
import org.meveo.service.script.Script;

/**
 * Script to perform data integrity check for custom tables during record creation or update. <br/>
 * Checks that another record does not exist overlapping same validity period (valid_from, valid_to fields)<br/>
 * Additional check can require a start date to be in the future<br/>
 * 
 * <p/>
 * Configuration steps<br/>
 * <ul>
 * <li>Create a new 'script type' notification. Specify the following values:</li>
 * <ul>
 * <li><b>Classname:</b> CustomEntityInstance</li>
 * <li><b>Event type filter:</b> Created or Updated</li>
 * <li><b>EL filter:</b> #{event.getCetCode().equalsIgnoreCase('custom entity table name')}
 * <li><b>Script instance:</b> CheckValidityDatesScript</li>
 * <li><b>Specify the following script parameters:</b>
 * <ul>
 * <li>field1, field2, field3... - field names to concider for matching record identification
 * <li>table - custom table name</li>
 * <li>pastDates: is optional. A value of 'true' will allow valid_from field to be in the past. A value of 'false' will accept valid_from field value in the future only. A default
 * value if not specified is 'false'
 * </ul>
 * <li>e.g. A custom table "Service type" does not allow validity overlap for the same code and service_id field combination. In addition, past dates are not allowed. <br/>
 * Script should contain the following parameters: field1: code, field2: service_id, pastDates: false, table: service_type
 * 
 * @author Hatim OUDAD
 */
@Stateless
public class CheckValidityDatesScript extends Script {

    private static final long serialVersionUID = -7270641380573534518L;

    @Inject
    private CustomTableService customTableService;

    @Override
    public void execute(Map<String, Object> initContext) throws ValidationException {
        try {
            // retrieve table name and fields
            CustomTableEvent ce = (CustomTableEvent) initContext.get("event");
            String table = (String) initContext.get("table");
            String pastDates = (String) initContext.get("pastDates");
            Map<String, Object> values = ce.getValues();
            initContext.keySet().removeIf(key -> !key.startsWith("field"));

            // Check if dates should be in the future
            if (pastDates == null || pastDates.equalsIgnoreCase("false")) {

                // Start date cannot be earlier than system date
                values.put("valid_from", DateUtils.setDateToStartOfDay((Date) values.get("valid_from")));
                Date startDate = (Date) values.get("valid_from");
                if (values.get("id") == null && (startDate == null || DateTimeComparator.getDateOnlyInstance().compare(startDate, new Date()) < 0)) {
                    throw new ValidationException("Start date cannot be null or earlier than system date");
                }

                // end date should be null or in the future
                Date endDate = null;
                if (values.get("valid_to") != null && !StringUtils.isBlank(values.get("valid_to"))) {
                    endDate = DateUtils.setDateToStartOfDay((Date) values.get("valid_to"));
                    values.put("valid_to", endDate);

                    if (DateTimeComparator.getDateOnlyInstance().compare(endDate, startDate) < 0) {
                        throw new ValidationException("end date should be null or in the future");
                    }
                }

            } else if (pastDates.equalsIgnoreCase("true")) {

                // Start date cannot be null
                values.put("valid_from", DateUtils.setDateToStartOfDay((Date) values.get("valid_from")));
                Date startDate = (Date) values.get("valid_from");
                if (values.get("id") == null && startDate == null) {
                    throw new ValidationException("Start date cannot be null");
                }

                // end date should be null or superior to start date
                Date endDate = null;
                if (values.get("valid_to") != null && !StringUtils.isBlank(values.get("valid_to"))) {
                    endDate = DateUtils.setDateToStartOfDay((Date) values.get("valid_to"));
                    values.put("valid_to", endDate);

                    if (DateTimeComparator.getDateOnlyInstance().compare(endDate, startDate) < 0) {
                        throw new ValidationException("end date cannot be earlier than start date");
                    }
                }

            }

            // check overlap
            if (!isOverlap(values, table, initContext, pastDates)) {
                throw new ValidationException("There is an overlapping with an existing entry");
            }

        } catch (Exception e) {
            throw new ValidationException(e.getMessage());

        }
    }

    /**
     * Indicate is the provided is overlap with an existing configuration line
     * 
     * @param initContext
     * @param pastDates true/false/null dates in the past are allowed to be configured
     * @param values fields that define a configuration line
     * @param table the code of the custom table
     *
     * @return true is the provided is overlap with an existing else if not.
     * @throws ParseException
     */
    public boolean isOverlap(Map<String, Object> values, String table, Map<String, Object> initContext, String pastDates) throws ParseException {
        // build query with specific fields
        final StringBuilder fieldsQuery = new StringBuilder();
        initContext.forEach((k, v) -> fieldsQuery.append(" and a." + v + (values.get(v) == null ? " is null" : (" = :" + v))));

        // check empty dates
        if (values.get("valid_from") == null && !StringUtils.isBlank(values.get("valid_from")) && values.get("valid_to") == null && !StringUtils.isBlank(values.get("valid_to"))) {
            return false;
        }

        // entry with an end date
        if (values.get("valid_from") != null && !StringUtils.isBlank(values.get("valid_from")) && values.get("valid_to") != null && !StringUtils.isBlank(values.get("valid_to"))) {

            List<String> results = null;
            String sql = "select * from " + table + " a  where " + " ((( a.valid_from IS NULL and a.valid_to IS NULL) " + "or  ( a.valid_from IS NULL and a.valid_to>:valid_from) "
                    + "or (a.valid_to IS NULL and a.valid_from<:valid_to) " + "or (a.valid_from IS NOT NULL and a.valid_to IS NOT NULL " + "and ((a.valid_from<=:valid_from " + "and :valid_from<a.valid_to) "
                    + "or (:valid_from<=a.valid_from " + "and a.valid_from<:valid_to)))) or a.valid_from = :valid_from)";

            if (fieldsQuery.length() != 0) {
                sql += fieldsQuery.toString();
            }

            Query query = customTableService.getEntityManager().createNativeQuery(sql);
            query.setParameter("valid_from", values.get("valid_from"));
            query.setParameter("valid_to", values.get("valid_to"));
            if (fieldsQuery.length() != 0) {
                for (Object fieldName : initContext.values()) {
                    Object value = values.get(fieldName);
                    if (value != null) {
                        query.setParameter((String) fieldName, value);
                    }
                }
            }

            results = query.getResultList();

            if (results != null && results.size() > 1) {
                return false;
            }
        }

        // entry without an end date
        if (values.get("valid_from") != null && !StringUtils.isBlank(values.get("valid_from")) && (values.get("valid_to") == null || StringUtils.isBlank(values.get("valid_to")))) {

            List<String> results = null;

            String sql = "select * from " + table + " a  where " + " ((((a.valid_from IS NULL and a.valid_to IS NULL) " + "or (a.valid_from<=:valid_from and :valid_from<a.valid_to) "
                    + "or (a.valid_from<=:valid_from and a.valid_to IS NULL) " + "or (a.valid_from IS NULL and :valid_from<a.valid_to)) or a.valid_from = :valid_from) ";
            if (pastDates != null && pastDates.equalsIgnoreCase("true")) {
                sql += "or (:valid_from <= a.valid_from))";
            } else {
                sql += ")";
            }

            if (fieldsQuery.length() != 0) {
                sql += fieldsQuery.toString();
            }

            Query query = customTableService.getEntityManager().createNativeQuery(sql);
            query.setParameter("valid_from", values.get("valid_from"));

            if (fieldsQuery.length() != 0) {
                for (Object fieldName : initContext.values()) {
                    if (values.get(fieldName) != null) {
                        query.setParameter((String) fieldName, values.get(fieldName));
                    }
                }
            }

            results = query.getResultList();

            if (results != null && results.size() > 1) {
                return false;
            }
        }

        return true;
    }

}
