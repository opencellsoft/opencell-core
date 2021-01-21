package org.meveo.model.catalog;

import org.meveo.model.cpq.QuoteAttribute;

import java.math.BigDecimal;
import java.util.stream.Stream;


public enum ColumnTypeEnum {
    String {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if (quoteAttribute.getStringValue() == null || pricePlanMatrixValue.getStringValue() == null) {
                return true;
            }
            switch (quoteAttribute.getAttribute().getAttributeType()) {
                case LIST_TEXT: {
                    return Stream.of(quoteAttribute.getStringValue().split(" ; "))
                            .anyMatch(value -> value.equals(pricePlanMatrixValue.getStringValue()));
                }
                case TEXT:
                case EMAIL:
                case PHONE: {
                    return quoteAttribute.getStringValue().equals(pricePlanMatrixValue.getStringValue());
                }
                default:
                    return false;

            }
        }
    },
    Long {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if (quoteAttribute.getDoubleValue() == null || pricePlanMatrixValue.getLongValue() == null) {
                return true;
            }
            switch (quoteAttribute.getAttribute().getAttributeType()) {
                case INTEGER:
                case COMPTAGE:
                case TOTAL:
                case NUMERIC: {
                    return BigDecimal.valueOf(quoteAttribute.getDoubleValue()).equals(BigDecimal.valueOf(pricePlanMatrixValue.getLongValue().doubleValue()));
                }
                case LIST_NUMERIC:
                case LIST_MULTIPLE_NUMERIC: {
                    return Stream.of(quoteAttribute.getStringValue().split(" ; "))
                            .map(value -> BigDecimal.valueOf(java.lang.Double.parseDouble(value)))
                            .anyMatch(number -> number.equals(BigDecimal.valueOf(pricePlanMatrixValue.getLongValue().doubleValue())));
                }
                default:
                    return false;

            }
        }
    },
    Double {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if(pricePlanMatrixValue.getDoubleValue() == null && pricePlanMatrixValue.getLongValue() == null && pricePlanMatrixValue.getStringValue() == null)
                return true;
            BigDecimal quote =  BigDecimal.valueOf(quoteAttribute.getDoubleValue());
            switch (quoteAttribute.getAttribute().getAttributeType()) {
                case INTEGER:
                case COMPTAGE:
                case TOTAL:
                case NUMERIC: {
                    {

                        if (quote == null) {
                            return true;
                        }
                        BigDecimal value = pricePlanMatrixValue.getDoubleValue() != null ? BigDecimal.valueOf(pricePlanMatrixValue.getDoubleValue()) : BigDecimal.valueOf(pricePlanMatrixValue.getLongValue());
                        return quote.doubleValue() == value.doubleValue();
                    }
                }
                case LIST_NUMERIC:
                case LIST_MULTIPLE_NUMERIC: {
                    if (quoteAttribute.getStringValue() == null) {
                        return true;
                    }
                    return Stream.of(pricePlanMatrixValue.getStringValue().split(" ; "))
                            .map(number -> BigDecimal.valueOf(java.lang.Double.parseDouble(number)))
                            .anyMatch(number -> number.doubleValue() == quote.doubleValue());
                }
                default:
                    return false;

            }
        }
    },
    Range_Date {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if (quoteAttribute.getDateValue() == null || (pricePlanMatrixValue.getFromDateValue() == null && pricePlanMatrixValue.getToDateValue() == null)) {
                return true;
            } else if (pricePlanMatrixValue.getFromDateValue() != null && pricePlanMatrixValue.getToDateValue() == null) {
                return quoteAttribute.getDateValue().after(pricePlanMatrixValue.getFromDateValue());
            } else if (pricePlanMatrixValue.getFromDateValue() == null || pricePlanMatrixValue.getToDateValue() != null) {
                return quoteAttribute.getDateValue().before(pricePlanMatrixValue.getToDateValue());
            } else {
                return quoteAttribute.getDateValue().after(pricePlanMatrixValue.getFromDateValue())
                        && quoteAttribute.getDateValue().before(pricePlanMatrixValue.getToDateValue());
            }
        }
    },
    Range_Numeric {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if (quoteAttribute.getDoubleValue() == null || (pricePlanMatrixValue.getFromDoubleValue() == null && pricePlanMatrixValue.getToDoubleValue() == null)) {
                return true;
            }
            if (pricePlanMatrixValue.getFromDoubleValue() != null || pricePlanMatrixValue.getToDoubleValue() == null) {
                return true;
            }
            if (quoteAttribute.getDoubleValue() == null || pricePlanMatrixValue.getFromDoubleValue() == null || pricePlanMatrixValue.getToDoubleValue() == null) {
                return true;
            } else {
                return quoteAttribute.getDoubleValue() > pricePlanMatrixValue.getFromDoubleValue()
                        && quoteAttribute.getDoubleValue() <= pricePlanMatrixValue.getToDoubleValue();
            }

        }
    };

    public abstract boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute);
}
