package org.meveo.apiv2.generic.core.filter.filtermapper;


import org.meveo.apiv2.generic.core.filter.FilterMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateMapper extends FilterMapper {
    public DateMapper(String property, Object value) {
        super(property, value);
    }
    @Override
    public Date mapStrategy(Object value) {
        if(value instanceof String) {
            try {
                return new SimpleDateFormat("dd/MM/yyyy").parse(String.valueOf(value));
            } catch (ParseException e) {
                throw new IllegalArgumentException(property + " has not a valid filter value, hint : dd/MM/yyyy");
            }
        }
        return new Date((Long) value);
    }
}

