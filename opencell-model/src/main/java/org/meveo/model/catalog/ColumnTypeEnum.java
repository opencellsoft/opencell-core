package org.meveo.model.catalog;

import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.QuoteAttribute;

import java.math.BigDecimal;
import java.util.stream.Stream;


public enum ColumnTypeEnum {
    String {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
            if (attributeValue.getStringValue() == null || pricePlanMatrixValue.getStringValue() == null) {
                return true;
            }
            switch (attributeValue.getAttribute().getAttributeType()) {
                case LIST_TEXT: {
                    return Stream.of(attributeValue.getStringValue().split(" ; "))
                            .anyMatch(value -> value.equals(pricePlanMatrixValue.getStringValue()));
                }
                case TEXT:
                case EMAIL:
                case PHONE: {
                    return attributeValue.getStringValue().equals(pricePlanMatrixValue.getStringValue());
                }
                default:
                    return false;

            }
        }
    },
    Long {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
            if (attributeValue.getDoubleValue() == null || pricePlanMatrixValue.getLongValue() == null) {
                return true;
            }
            switch (attributeValue.getAttribute().getAttributeType()) {
                case INTEGER:
                case COUNT:
                case TOTAL:
                case NUMERIC: {
                    return BigDecimal.valueOf(attributeValue.getDoubleValue()).equals(BigDecimal.valueOf(pricePlanMatrixValue.getLongValue().doubleValue()));
                }
                case LIST_NUMERIC:
                case LIST_MULTIPLE_NUMERIC: {
                    return Stream.of(attributeValue.getStringValue().split(" ; "))
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
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
            if(pricePlanMatrixValue.getDoubleValue() == null && pricePlanMatrixValue.getLongValue() == null && pricePlanMatrixValue.getStringValue() == null)
                return true;
            BigDecimal quote =  BigDecimal.valueOf(attributeValue.getDoubleValue());
            switch (attributeValue.getAttribute().getAttributeType()) {
                case INTEGER:
                case COUNT:
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
                    if (attributeValue.getStringValue() == null) {
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
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
            if (attributeValue.getDateValue() == null || (pricePlanMatrixValue.getFromDateValue() == null && pricePlanMatrixValue.getToDateValue() == null)) {
                return true;
            } else if (pricePlanMatrixValue.getFromDateValue() != null && pricePlanMatrixValue.getToDateValue() == null) {
                return attributeValue.getDateValue().after(pricePlanMatrixValue.getFromDateValue());
            } else if (pricePlanMatrixValue.getFromDateValue() == null || pricePlanMatrixValue.getToDateValue() != null) {
                return attributeValue.getDateValue().before(pricePlanMatrixValue.getToDateValue());
            } else {
                return attributeValue.getDateValue().after(pricePlanMatrixValue.getFromDateValue())
                        && attributeValue.getDateValue().before(pricePlanMatrixValue.getToDateValue());
            }
        }
    },
    Range_Numeric {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
            if (attributeValue.getDoubleValue() == null || (pricePlanMatrixValue.getFromDoubleValue() == null && pricePlanMatrixValue.getToDoubleValue() == null)) {
                return true;
            }
            if (pricePlanMatrixValue.getFromDoubleValue() != null || pricePlanMatrixValue.getToDoubleValue() == null) {
                return true;
            }
            if (attributeValue.getDoubleValue() == null || pricePlanMatrixValue.getFromDoubleValue() == null || pricePlanMatrixValue.getToDoubleValue() == null) {
                return true;
            } else {
                return attributeValue.getDoubleValue() > pricePlanMatrixValue.getFromDoubleValue()
                        && attributeValue.getDoubleValue() <= pricePlanMatrixValue.getToDoubleValue();
            }

        }
    };

    public abstract boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue);
}
