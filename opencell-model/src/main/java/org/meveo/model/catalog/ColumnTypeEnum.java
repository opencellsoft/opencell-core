package org.meveo.model.catalog;

public enum ColumnTypeEnum {
    String,
    Long{
        @Override
        public String getValue(PricePlanMatrixValue pricePlanMatrixValue) {
            return pricePlanMatrixValue.getLongValue().toString();
        }
    },
    Double{
        @Override
        public String getValue(PricePlanMatrixValue pricePlanMatrixValue) {
            return pricePlanMatrixValue.getDoubleValue().toString();
        }
    },
    Range{
        @Override
        public java.lang.String getValue(PricePlanMatrixValue pricePlanMatrixValue) {
            if(pricePlanMatrixValue.getFromDateValue() != null)
                return pricePlanMatrixValue.getFromDateValue() + " - " + pricePlanMatrixValue.getToDateValue();
            else
                return pricePlanMatrixValue.getFromDoubleValue() + " - " + pricePlanMatrixValue.getToDoubleValue();
        }
    };

    public String getValue(PricePlanMatrixValue pricePlanMatrixValue) {
        return pricePlanMatrixValue.getStringValue();
    }
}
