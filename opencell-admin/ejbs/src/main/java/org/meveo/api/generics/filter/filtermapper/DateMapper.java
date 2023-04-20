package org.meveo.api.generics.filter.filtermapper;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.meveo.api.generics.filter.FilterMapper;

public class DateMapper extends FilterMapper {
    public DateMapper(String property, Object value) {
        super(property, value);
    }
    @Override
    public Date mapStrategy(Object value) {
        if(value instanceof String) {
            try {
                return ((String) value).matches("^\\d{4}-\\d{2}-\\d{2}$") ? new SimpleDateFormat("yyyy-MM-dd").parse(String.valueOf(value)) : new SimpleDateFormat("dd/MM/yyyy").parse(String.valueOf(value));
            } catch (ParseException e) {
                throw new IllegalArgumentException(property + " has not a valid filter value, hint : yyyy-MM-dd or dd/MM/yyyy");
            }
        }
        return new Date((Long) value);
    }
}

