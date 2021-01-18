package org.meveo.model.catalog;

import org.meveo.model.cpq.QuoteAttribute;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.meveo.model.cpq.QuoteAttribute.MatchingTypeEnum.*;

public enum ColumnTypeEnum {
    String {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if (quoteAttribute.getStringValue() == null || pricePlanMatrixValue.getStringValue() == null) {
                quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                return true;
            }
            switch (quoteAttribute.getAttribute().getAttributeType()) {
                case LIST_TEXT: {
                    quoteAttribute.setMatchingTypeEnum(RANGE_VALUE);
                    return Stream.of(quoteAttribute.getStringValue().split(" ; "))
                            .anyMatch(value -> value.equals(pricePlanMatrixValue.getStringValue()));
                }
                case TEXT:
                case EMAIL:
                case PHONE: {
                    quoteAttribute.setMatchingTypeEnum(EXACT_VALUE);
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
                quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                return true;
            }
            switch (quoteAttribute.getAttribute().getAttributeType()) {
                case INTEGER:
                case COMPTAGE:
                case TOTAL:
                case NUMERIC: {
                    quoteAttribute.setMatchingTypeEnum(EXACT_VALUE);
                    return BigDecimal.valueOf(quoteAttribute.getDoubleValue()).equals(BigDecimal.valueOf(pricePlanMatrixValue.getLongValue().doubleValue()));
                }
                case LIST_NUMERIC:
                case LIST_MULTIPLE_NUMERIC: {
                    quoteAttribute.setMatchingTypeEnum(RANGE_VALUE);
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
            if (pricePlanMatrixValue.getDoubleValue() == null) {
                quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                return true;
            }
            switch (quoteAttribute.getAttribute().getAttributeType()) {
                case INTEGER:
                case COMPTAGE:
                case TOTAL:
                case NUMERIC: {
                    {
                        if (quoteAttribute.getDoubleValue() == null) {
                            quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                            return true;
                        }
                        quoteAttribute.setMatchingTypeEnum(EXACT_VALUE);
                        return BigDecimal.valueOf(quoteAttribute.getDoubleValue()).equals(BigDecimal.valueOf(pricePlanMatrixValue.getDoubleValue()));
                    }
                }
                case LIST_NUMERIC:
                case LIST_MULTIPLE_NUMERIC: {
                    if (quoteAttribute.getStringValue() == null) {
                        quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                        return true;
                    }
                    quoteAttribute.setMatchingTypeEnum(RANGE_VALUE);
                    return Stream.of(quoteAttribute.getStringValue().split(" ; "))
                            .map(value -> BigDecimal.valueOf(java.lang.Double.parseDouble(value)))
                            .anyMatch(number -> number.equals(BigDecimal.valueOf(pricePlanMatrixValue.getDoubleValue())));
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
                quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                return true;
            } else if (pricePlanMatrixValue.getFromDateValue() != null && pricePlanMatrixValue.getToDateValue() == null) {
                quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                return quoteAttribute.getDateValue().after(pricePlanMatrixValue.getFromDateValue());
            } else if (pricePlanMatrixValue.getFromDateValue() == null || pricePlanMatrixValue.getToDateValue() != null) {
                quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                return quoteAttribute.getDateValue().before(pricePlanMatrixValue.getToDateValue());
            } else {
                quoteAttribute.setMatchingTypeEnum(RANGE_VALUE);
                return quoteAttribute.getDateValue().after(pricePlanMatrixValue.getFromDateValue())
                        && quoteAttribute.getDateValue().before(pricePlanMatrixValue.getToDateValue());
            }
        }
    },
    Range_Numeric {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute) {
            if (quoteAttribute.getDoubleValue() == null || (pricePlanMatrixValue.getFromDoubleValue() == null && pricePlanMatrixValue.getToDoubleValue() == null)) {
                quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                return true;
            }
            if (pricePlanMatrixValue.getFromDoubleValue() != null || pricePlanMatrixValue.getToDoubleValue() == null) {
                quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                return true;
            }
            if (quoteAttribute.getDoubleValue() == null || pricePlanMatrixValue.getFromDoubleValue() == null || pricePlanMatrixValue.getToDoubleValue() == null) {
                quoteAttribute.setMatchingTypeEnum(REG_MATCHING);
                return true;
            } else {
                quoteAttribute.setMatchingTypeEnum(RANGE_VALUE);
                return quoteAttribute.getDoubleValue() > pricePlanMatrixValue.getFromDoubleValue()
                        && quoteAttribute.getDoubleValue() <= pricePlanMatrixValue.getToDoubleValue();
            }

        }
    };

    public abstract boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, QuoteAttribute quoteAttribute);
}
