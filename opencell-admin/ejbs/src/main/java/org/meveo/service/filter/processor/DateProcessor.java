package org.meveo.service.filter.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.meveo.admin.exception.FilterException;
import org.meveo.commons.utils.FilteredQueryBuilder;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.filter.PrimitiveFilterCondition;

public class DateProcessor extends PrimitiveFilterProcessor {

    public static final String PREFIX = "date:";

    @Override
    public boolean canProccessCondition(PrimitiveFilterCondition condition) {
        return isPrefixInOperand(condition, PREFIX);
    }

    @Override
    public void process(FilteredQueryBuilder queryBuilder, String alias, PrimitiveFilterCondition condition) throws FilterException {
        try {
            ParamBean parameters = ParamBean.getInstance();
            String strDateValue = condition.getOperand().substring(PREFIX.length());
            Date dateValue = null;

            SimpleDateFormat sdf = new SimpleDateFormat(parameters.getProperty("meveo.dateFormat", "dd/MM/yyyy"));
            try {
                dateValue = sdf.parse(strDateValue);
            } catch (ParseException e) {
                try {
                    sdf = new SimpleDateFormat(parameters.getProperty("meveo.dateTimeFormat", "dd/MM/yyyy HH:mm:ss"));
                    dateValue = sdf.parse(strDateValue);
                } catch (ParseException e1) {
                    throw new FilterException(e1.getMessage());
                }
            }
            buildQuery(queryBuilder, condition, dateValue);
        } catch (Exception e) {
            throw new FilterException(e.getMessage());
        }
    }

    protected void buildQuery(FilteredQueryBuilder queryBuilder, PrimitiveFilterCondition condition, Date dateValue){
        if ("=".equals(condition.getOperator())) {
            queryBuilder.addCriterionDateTruncatedToDay(condition.getFieldName(), dateValue);
        } else if (">=".equals(condition.getOperator())) {
            queryBuilder.addCriterionDateRangeFromTruncatedToDay(condition.getFieldName(), dateValue);
        } else if ("<=".equals(condition.getOperator())) {
            queryBuilder.addCriterionDateRangeToTruncatedToDay(condition.getFieldName(), dateValue);
        }
    }
}
