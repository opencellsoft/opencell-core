package org.meveo.model.catalog;

import org.meveo.model.cpq.QuoteAttribute;

import java.math.BigDecimal;
import java.util.stream.Stream;

public enum ColumnTypeEnum {
    String {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if(quoteAttribute.getStringValue() == null || pricePlanMatrixValue.getStringValue() == null)
                return false;
            switch(quoteAttribute.getAttribute().getAttributeType()){
                case LIST_TEXT: return Stream.of(quoteAttribute.getStringValue().split(" ; "))
                                    .anyMatch(value -> value.equals(pricePlanMatrixValue.getStringValue()));
                case TEXT:
                case EMAIL:
                case PHONE: return quoteAttribute.getStringValue().equals(pricePlanMatrixValue.getStringValue());
                default:return false;

            }
        }
    },
    Long {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if(quoteAttribute.getDoubleValue() == null || pricePlanMatrixValue.getLongValue() == null)
                return false;
            switch(quoteAttribute.getAttribute().getAttributeType()){
                case INTEGER:
                case COMPTAGE:
                case TOTAL:
                case NUMERIC:
                    return BigDecimal.valueOf(quoteAttribute.getDoubleValue()).equals(BigDecimal.valueOf(pricePlanMatrixValue.getLongValue().doubleValue()));
                case LIST_NUMERIC:
                case LIST_MULTIPLE_NUMERIC: {
                    return Stream.of(quoteAttribute.getStringValue().split(" ; "))
                                                        .map(value -> BigDecimal.valueOf(java.lang.Double.parseDouble(value)))
                                                        .anyMatch(number -> number.equals(BigDecimal.valueOf(pricePlanMatrixValue.getLongValue().doubleValue())));
                }
                default:return false;

            }
        }
    },
    Double {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if(pricePlanMatrixValue.getDoubleValue() == null)
                return false;
            switch(quoteAttribute.getAttribute().getAttributeType()){
                case INTEGER:
                case COMPTAGE:
                case TOTAL:
                case NUMERIC:
                     {
                         if(quoteAttribute.getDoubleValue() == null)
                             return false;
                         return BigDecimal.valueOf(quoteAttribute.getDoubleValue()).equals(BigDecimal.valueOf(pricePlanMatrixValue.getDoubleValue()));
                     }
                case LIST_NUMERIC:
                case LIST_MULTIPLE_NUMERIC: {
                        if (quoteAttribute.getStringValue() == null)
                            return false;
                        return Stream.of(quoteAttribute.getStringValue().split(" ; "))
                        .map(value -> BigDecimal.valueOf(java.lang.Double.parseDouble(value)))
                        .anyMatch(number -> number.equals(BigDecimal.valueOf(pricePlanMatrixValue.getDoubleValue())));
                }
                default:return false;

            }
        }
    },
    Range_Date {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if(quoteAttribute.getDateValue() == null || pricePlanMatrixValue.getFromDateValue() == null || pricePlanMatrixValue.getToDateValue() == null)
                return false;
            switch(quoteAttribute.getAttribute().getAttributeType()){
                case DATE: return quoteAttribute.getDateValue().after(pricePlanMatrixValue.getFromDateValue())
                                    && quoteAttribute.getDateValue().before(pricePlanMatrixValue.getToDateValue());
                default:return false;

            }
        }
    },
    Range_Numeric {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if(quoteAttribute.getDoubleValue()  == null || pricePlanMatrixValue.getFromDoubleValue() == null || pricePlanMatrixValue.getToDoubleValue() == null)
                return false;
            switch(quoteAttribute.getAttribute().getAttributeType()){
                case DATE: return quoteAttribute.getDoubleValue() > pricePlanMatrixValue.getFromDoubleValue()
                        && quoteAttribute.getDoubleValue() <= pricePlanMatrixValue.getToDoubleValue();
                default:return false;

            }
        }
    };

    public abstract boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute);
}
