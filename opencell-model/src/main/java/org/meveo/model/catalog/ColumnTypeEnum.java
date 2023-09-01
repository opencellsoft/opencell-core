package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.cpq.AttributeValue;


public enum ColumnTypeEnum {
    String {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
        	String multiValuesAttributeSeparator = ParamBean.getInstance().getProperty("attribute.multivalues.separator", ";");
            if (attributeValue.getStringValue() == null && pricePlanMatrixValue.getStringValue() == null) {
                return true;
            } else if (attributeValue.getStringValue() == null || pricePlanMatrixValue.getStringValue() == null) {
                return false;
            }
            switch (attributeValue.getAttribute().getAttributeType()) {
                case LIST_MULTIPLE_TEXT:
                case LIST_TEXT: {
                    return Stream.of(attributeValue.getStringValue().split(multiValuesAttributeSeparator))
                            .anyMatch(value -> value.equals(pricePlanMatrixValue.getStringValue()));
                }
                case TEXT:
                case EMAIL:
                case INFO:
                case PHONE: {
                    return attributeValue.getStringValue().equals(pricePlanMatrixValue.getStringValue());
                }
                case EXPRESSION_LANGUAGE: {
                	System.out.println("ColumnTypeEnum valueMatch="+pricePlanMatrixValue.getStringValue());
                	 if(attributeValue.getDoubleValue()!=null) {
                		 System.out.println("ColumnTypeEnum valueMatch double="+attributeValue.getDoubleValue());
	       				 return java.lang.Double.valueOf(pricePlanMatrixValue.getStringValue()).equals(attributeValue.getDoubleValue());
	       			 }else if(attributeValue.getBooleanValue()!=null) {
	       					return attributeValue.getBooleanValue().equals(java.lang.Boolean.valueOf(pricePlanMatrixValue.getStringValue()));
	    	       	 }else {
	    	       		System.out.println("ColumnTypeEnum valueMatch string="+attributeValue.getStringValue());
	    	       		return pricePlanMatrixValue.getStringValue().equals(attributeValue.getStringValue());
	       			}
                }
                default:
                    return false;

            }
        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValue pricePlanMatrixValue) {
            return StringUtils.isEmpty(pricePlanMatrixValue.getStringValue());
        }
    },
    Long {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
        	String multiValuesAttributeSeparator = ParamBean.getInstance().getProperty("attribute.multivalues.separator", ";");
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
                    return Stream.of(attributeValue.getStringValue().split(multiValuesAttributeSeparator))
                            .map(value -> BigDecimal.valueOf(java.lang.Double.parseDouble(value)))
                            .anyMatch(number -> number.equals(BigDecimal.valueOf(pricePlanMatrixValue.getLongValue().doubleValue())));
                }
                default:
                    return false;

            }
        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValue pricePlanMatrixValue) {
            return pricePlanMatrixValue.getDoubleValue() == null;
        }
    },
    Double {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
        	String multiValuesAttributeSeparator = ParamBean.getInstance().getProperty("attribute.multivalues.separator", ";");
            if(pricePlanMatrixValue.getDoubleValue() == null && pricePlanMatrixValue.getLongValue() == null && StringUtils.isEmpty(pricePlanMatrixValue.getStringValue()))
                return true;
            Object quote =  attributeValue.getAttribute().getAttributeType().getValue(attributeValue);
            switch (attributeValue.getAttribute().getAttributeType()) {
                case INTEGER:
                case COUNT:
                case TOTAL:
                case LIST_NUMERIC:
                case NUMERIC: {
                    {

                        if (quote == null) {
                            return true;
                        }
                        BigDecimal value = pricePlanMatrixValue.getDoubleValue() != null ? BigDecimal.valueOf(pricePlanMatrixValue.getDoubleValue()) : BigDecimal.valueOf(pricePlanMatrixValue.getLongValue());
                        return ColumnTypeEnum.parseValue(quote) == value.doubleValue();
                    }
                }
                case LIST_MULTIPLE_NUMERIC: {
                    if (attributeValue.getStringValue() == null) {
                        return true;
                    }
                    return Stream.of(pricePlanMatrixValue.getStringValue().split(multiValuesAttributeSeparator))
                            .map(number -> BigDecimal.valueOf(java.lang.Double.parseDouble(number)))
                            .anyMatch(number -> {
                                double value = parseValue(quote);
                                return number.doubleValue() == value;
                            });
                }
                default:
                    return false;

            }
        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValue pricePlanMatrixValue) {
            return pricePlanMatrixValue.getDoubleValue() == null && pricePlanMatrixValue.getLongValue() == null && StringUtils.isEmpty(pricePlanMatrixValue.getStringValue());
        }
    },
    Range_Date {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
            if (attributeValue.getDateValue() == null || (pricePlanMatrixValue.getFromDateValue() == null && pricePlanMatrixValue.getToDateValue() == null)) {
                return true;
            } else if (pricePlanMatrixValue.getFromDateValue() != null && pricePlanMatrixValue.getToDateValue() == null) {
                return attributeValue.getDateValue().equals(pricePlanMatrixValue.getFromDateValue()) || attributeValue.getDateValue().after(pricePlanMatrixValue.getFromDateValue());
            } else if (pricePlanMatrixValue.getFromDateValue() == null && pricePlanMatrixValue.getToDateValue() != null) {
                return attributeValue.getDateValue().equals(pricePlanMatrixValue.getToDateValue()) || attributeValue.getDateValue().before(pricePlanMatrixValue.getToDateValue());
            } else {
                return (attributeValue.getDateValue().equals(pricePlanMatrixValue.getFromDateValue()) || attributeValue.getDateValue().after(pricePlanMatrixValue.getFromDateValue()))
                        && (attributeValue.getDateValue().equals(pricePlanMatrixValue.getToDateValue()) || attributeValue.getDateValue().before(pricePlanMatrixValue.getToDateValue()));
            }
        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValue pricePlanMatrixValue) {
            return pricePlanMatrixValue.getFromDateValue() == null && pricePlanMatrixValue.getToDateValue() == null;
        }
    },
    Range_Numeric {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
            boolean excludeMaxValue =
                    ParamBean.getInstance().getPropertyAsBoolean("pricePlan.rangeMode.excludeTheMaxValue", true);
            if (attributeValue.getDoubleValue() == null && (pricePlanMatrixValue.getFromDoubleValue() == null
                    && pricePlanMatrixValue.getToDoubleValue() == null)) {
                return true;
            }
            if (attributeValue.getDoubleValue() != null && pricePlanMatrixValue.getFromDoubleValue() != null
                    && pricePlanMatrixValue.getToDoubleValue() == null
                    &&  attributeValue.getDoubleValue() >= pricePlanMatrixValue.getFromDoubleValue()) {
                return true;
            }
            if (attributeValue.getDoubleValue() != null && pricePlanMatrixValue.getFromDoubleValue() == null
                    && pricePlanMatrixValue.getToDoubleValue() != null) {
                if(excludeMaxValue) {
                    if(attributeValue.getDoubleValue() < pricePlanMatrixValue.getToDoubleValue()) {
                        return true;
                    }
                } else {
                    if(attributeValue.getDoubleValue() <= pricePlanMatrixValue.getToDoubleValue()) {
                        return true;
                    }
                }
            }
            if(attributeValue.getDoubleValue() != null && pricePlanMatrixValue.getFromDoubleValue() != null
                    && pricePlanMatrixValue.getToDoubleValue() != null
                    && attributeValue.getDoubleValue() >= pricePlanMatrixValue.getFromDoubleValue()){
                if(excludeMaxValue) {
                    if(attributeValue.getDoubleValue() < pricePlanMatrixValue.getToDoubleValue()) {
                        return true;
                    }
                } else {
                    if(attributeValue.getDoubleValue() <= pricePlanMatrixValue.getToDoubleValue()) {
                        return true;
                    }
                }
            }
           return false;

        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValue pricePlanMatrixValue) {
            return pricePlanMatrixValue.getFromDoubleValue() == null && pricePlanMatrixValue.getToDoubleValue() == null;
        }
    },
    Boolean {
        @Override
        public boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue) {
            if (attributeValue.getStringValue() == null || StringUtils.isEmpty(pricePlanMatrixValue.getStringValue())) {
                return true;
            }
            return attributeValue.getStringValue().equalsIgnoreCase(pricePlanMatrixValue.getStringValue());
        }

        @Override
        public boolean matchWithAllValues(PricePlanMatrixValue pricePlanMatrixValue) {
            return StringUtils.isEmpty(pricePlanMatrixValue.getStringValue());
        }
    };

    private static double parseValue(Object quote) {
        double value;
        if(quote instanceof String) {
            value = java.lang.Double.parseDouble((String) quote);
        } else {
            value = (Double) quote;
        }
        return value;
    }

    public abstract boolean valueMatch(PricePlanMatrixValue pricePlanMatrixValue, AttributeValue attributeValue);

    public abstract boolean matchWithAllValues(PricePlanMatrixValue pricePlanMatrixValue);
}
