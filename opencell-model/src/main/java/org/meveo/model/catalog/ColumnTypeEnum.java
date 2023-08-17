package org.meveo.model.catalog;

import org.apache.commons.lang3.StringUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.cpq.AttributeValue;

import java.math.BigDecimal;
import java.util.stream.Stream;


public enum ColumnTypeEnum {
    String {
        @Override
        public boolean valueMatch(PricePlanMatrixValueForRating pricePlanMatrixValue, AttributeValue attributeValue) {
            String multiValuesAttributeSeparator = ";"; //ParamBean.getInstance().getProperty("attribute.multivalues.separator", ";");
            if (StringUtils.isEmpty(pricePlanMatrixValue.getStringValue())) {
                return true;
            }
            switch (attributeValue.getAttribute().getAttributeType()) {
                case LIST_MULTIPLE_TEXT:
                case LIST_TEXT: {
                    return !StringUtils.isEmpty(attributeValue.getStringValue()) &&
                            Stream.of(attributeValue.getStringValue().split(multiValuesAttributeSeparator))
                                    .anyMatch(value -> value.equals(pricePlanMatrixValue.getStringValue()));
                }
                case TEXT:
                case EMAIL:
                case INFO:
                case PHONE: {
                    return pricePlanMatrixValue.getStringValue().equals(attributeValue.getStringValue());
                }
                case EXPRESSION_LANGUAGE: {
                    if (attributeValue.getDoubleValue() != null) {
                        try {
                            return attributeValue.getDoubleValue().equals(java.lang.Double.valueOf(pricePlanMatrixValue.getStringValue()));
                        } catch (NumberFormatException nfe) {
                            return false;
                        }
                    } else if (attributeValue.getBooleanValue() != null) {
                        return attributeValue.getBooleanValue().equals(java.lang.Boolean.valueOf(pricePlanMatrixValue.getStringValue()));
                    } else {
                        return pricePlanMatrixValue.getStringValue().equals(attributeValue.getStringValue());
                    }
                }
                default:
                    return false;

            }
        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValueForRating pricePlanMatrixValue) {
            return StringUtils.isEmpty(pricePlanMatrixValue.getStringValue());
        }
    },
    Long {
        @Override
        public boolean valueMatch(PricePlanMatrixValueForRating pricePlanMatrixValue, AttributeValue attributeValue) {
            String multiValuesAttributeSeparator = ";"; //ParamBean.getInstance().getProperty("attribute.multivalues.separator", ";");
            if (pricePlanMatrixValue.getDoubleValue() == null) {
                return true;
            }
            switch (attributeValue.getAttribute().getAttributeType()) {
                case INTEGER:
                case COUNT:
                case TOTAL:
                case NUMERIC: {
                    return attributeValue.getDoubleValue() != null &&
                            (BigDecimal.valueOf(attributeValue.getDoubleValue()).equals(BigDecimal.valueOf(pricePlanMatrixValue.getDoubleValue())));
                }
                case LIST_NUMERIC:
                case LIST_MULTIPLE_NUMERIC: {
                    return !StringUtils.isEmpty(attributeValue.getStringValue()) &&
                            Stream.of(attributeValue.getStringValue().split(multiValuesAttributeSeparator))
                                    .map(value -> BigDecimal.valueOf(java.lang.Double.parseDouble(value)))
                                    .anyMatch(number -> number.equals(BigDecimal.valueOf(pricePlanMatrixValue.getDoubleValue())));
                }
                default:
                    return false;

            }
        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValueForRating pricePlanMatrixValue) {
            return pricePlanMatrixValue.getDoubleValue() == null;
        }
    },
    Double {
        @Override
        public boolean valueMatch(PricePlanMatrixValueForRating pricePlanMatrixValue, AttributeValue attributeValue) {
            String multiValuesAttributeSeparator = ";"; //ParamBean.getInstance().getProperty("attribute.multivalues.separator", ";");
            if (pricePlanMatrixValue.getDoubleValue() == null && pricePlanMatrixValue.getLongValue() == null) {
                return true;
            }
            Object passedAttributeValue = attributeValue.getAttribute().getAttributeType().getValue(attributeValue);
            switch (attributeValue.getAttribute().getAttributeType()) {
                case INTEGER:
                case COUNT:
                case TOTAL:
                case LIST_NUMERIC:
                case NUMERIC: {
                    {
                        if (Boolean.isNullOrContainsEmptyString(passedAttributeValue)) {
                            return false;
                        }
                        BigDecimal value = pricePlanMatrixValue.getDoubleValue() != null ? BigDecimal.valueOf(pricePlanMatrixValue.getDoubleValue()) :
                                BigDecimal.valueOf(pricePlanMatrixValue.getLongValue());
                        return ColumnTypeEnum.parseValue(passedAttributeValue) == value.doubleValue();
                    }
                }
                case LIST_MULTIPLE_NUMERIC: {
                    if (Boolean.isNullOrContainsEmptyString(passedAttributeValue)) {
                        return false;
                    }
                    return !StringUtils.isEmpty(pricePlanMatrixValue.getStringValue()) &&
                            Stream.of(pricePlanMatrixValue.getStringValue().split(multiValuesAttributeSeparator))
                                    .map(number -> BigDecimal.valueOf(java.lang.Double.parseDouble(number)))
                                    .anyMatch(number -> {
                                        double value = parseValue(passedAttributeValue);
                                        return number.doubleValue() == value;
                                    });
                }
                default:
                    return false;

            }
        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValueForRating pricePlanMatrixValue) {
            return pricePlanMatrixValue.getDoubleValue() == null;
        }
    },
    Range_Date {
        @Override
        public boolean valueMatch(PricePlanMatrixValueForRating pricePlanMatrixValue, AttributeValue attributeValue) {
            if (pricePlanMatrixValue.getFromDateValue() == null && pricePlanMatrixValue.getToDateValue() == null) {
                return true;
            } else if (attributeValue.getDateValue() == null) {
                return false;
            } else if (pricePlanMatrixValue.getFromDateValue() != null && pricePlanMatrixValue.getToDateValue() == null) {
                return attributeValue.getDateValue().equals(pricePlanMatrixValue.getFromDateValue()) || attributeValue.getDateValue().after(pricePlanMatrixValue.getFromDateValue());
            } else if (pricePlanMatrixValue.getFromDateValue() == null || pricePlanMatrixValue.getToDateValue() != null) {
                return attributeValue.getDateValue().equals(pricePlanMatrixValue.getToDateValue()) || attributeValue.getDateValue().before(pricePlanMatrixValue.getToDateValue());
            } else {
                return (attributeValue.getDateValue().equals(pricePlanMatrixValue.getFromDateValue()) || attributeValue.getDateValue().after(pricePlanMatrixValue.getFromDateValue()))
                        && (attributeValue.getDateValue().equals(pricePlanMatrixValue.getToDateValue()) || attributeValue.getDateValue().before(pricePlanMatrixValue.getToDateValue()));
            }
        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValueForRating pricePlanMatrixValue) {
            return pricePlanMatrixValue.getFromDateValue() == null && pricePlanMatrixValue.getToDateValue() == null;
        }
    },
    Range_Numeric {
        @Override
        public boolean valueMatch(PricePlanMatrixValueForRating pricePlanMatrixValue, AttributeValue attributeValue) {
            boolean excludeMaxValue = ParamBean.getInstance().getPropertyAsBoolean("pricePlan.rangeMode.excludeTheMaxValue", true);
            if (pricePlanMatrixValue.getFromDoubleValue() == null && pricePlanMatrixValue.getToDoubleValue() == null) {
                return true;
            } else if (attributeValue.getDoubleValue() == null) {
                return false;
            }
            if (pricePlanMatrixValue.getFromDoubleValue() != null
                    && pricePlanMatrixValue.getToDoubleValue() == null
                    && attributeValue.getDoubleValue() >= pricePlanMatrixValue.getFromDoubleValue()) {
                return true;
            }
            if (pricePlanMatrixValue.getFromDoubleValue() == null
                    && pricePlanMatrixValue.getToDoubleValue() != null) {
                if (excludeMaxValue) {
                    if (attributeValue.getDoubleValue() < pricePlanMatrixValue.getToDoubleValue()) {
                        return true;
                    }
                } else {
                    if (attributeValue.getDoubleValue() <= pricePlanMatrixValue.getToDoubleValue()) {
                        return true;
                    }
                }
            }
            if (pricePlanMatrixValue.getFromDoubleValue() != null
                    && pricePlanMatrixValue.getToDoubleValue() != null
                    && attributeValue.getDoubleValue() >= pricePlanMatrixValue.getFromDoubleValue()) {
                if (excludeMaxValue) {
                    if (attributeValue.getDoubleValue() < pricePlanMatrixValue.getToDoubleValue()) {
                        return true;
                    }
                } else {
                    if (attributeValue.getDoubleValue() <= pricePlanMatrixValue.getToDoubleValue()) {
                        return true;
                    }
                }
            }
            return false;

        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValueForRating pricePlanMatrixValue) {
            return pricePlanMatrixValue.getFromDoubleValue() == null && pricePlanMatrixValue.getToDoubleValue() == null;
        }
    },
    Boolean {
        @Override
        public boolean valueMatch(PricePlanMatrixValueForRating pricePlanMatrixValue, AttributeValue attributeValue) {
            if (pricePlanMatrixValue.getBooleanValue() == null) {
                return true;
            }
            return pricePlanMatrixValue.getBooleanValue() == attributeValue.getBooleanValue();
        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValueForRating pricePlanMatrixValue) {
            return pricePlanMatrixValue.getBooleanValue() == null;
        }
    };

    private static double parseValue(Object quote) {
        double value;
        if (quote instanceof String) {
            value = java.lang.Double.parseDouble((String) quote);
        } else {
            value = (Double) quote;
        }
        return value;
    }

    public abstract boolean valueMatch(PricePlanMatrixValueForRating pricePlanMatrixValue, AttributeValue attributeValue);

    public abstract boolean matchWithAllValues(PricePlanMatrixValueForRating pricePlanMatrixValue);

    private boolean isNullOrContainsEmptyString(Object value) {
        return value == null || (value instanceof String && ((String) value).isEmpty());

    }
}
