package org.meveo.model.catalog;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.meveo.model.cpq.AttributeValue;

public class PricePlanMatrixValueForRating implements Serializable {

    private static final long serialVersionUID = -3841571425197748532L;

    private long attributeId;

    private ColumnTypeEnum pricePlanMatrixColumnType;

    private long pricePlanMatrixLine;

    private Long longValue;

    private Double doubleValue;

    private String stringValue;

    private Date dateValue;

    private Date fromDateValue;

    private Date toDateValue;

    private Double fromDoubleValue;

    private Double toDoubleValue;

    protected Boolean booleanValue;

    public PricePlanMatrixValueForRating() {
    }

    public PricePlanMatrixValueForRating(Long attributeId, ColumnTypeEnum pricePlanMatrixColumnType, Long pricePlanMatrixLine, Long longValue, Double doubleValue, String stringValue, Date dateValue, Date fromDateValue,
            Date toDateValue, Double fromDoubleValue, Double toDoubleValue, Boolean booleanValue) {

        this.attributeId = attributeId;
        this.pricePlanMatrixColumnType = pricePlanMatrixColumnType;
        this.pricePlanMatrixLine = pricePlanMatrixLine;
        this.longValue = longValue;
        this.doubleValue = doubleValue;
        this.stringValue = stringValue;
        this.dateValue = dateValue;
        this.fromDateValue = fromDateValue;
        this.toDateValue = toDateValue;
        this.fromDoubleValue = fromDoubleValue;
        this.toDoubleValue = toDoubleValue;
        this.booleanValue = booleanValue;
    }

    public long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(long attributeId) {
        this.attributeId = attributeId;
    }

    public ColumnTypeEnum getPricePlanMatrixColumnType() {
        return pricePlanMatrixColumnType;
    }

    public void setPricePlanMatrixColumnType(ColumnTypeEnum pricePlanMatrixColumnType) {
        this.pricePlanMatrixColumnType = pricePlanMatrixColumnType;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public long getPricePlanMatrixLine() {
        return pricePlanMatrixLine;
    }

    public void setPricePlanMatrixLine(long pricePlanMatrixLine) {
        this.pricePlanMatrixLine = pricePlanMatrixLine;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public Date getFromDateValue() {
        return fromDateValue;
    }

    public void setFromDateValue(Date fromDateValue) {
        this.fromDateValue = fromDateValue;
    }

    public Date getToDateValue() {
        return toDateValue;
    }

    public void setToDateValue(Date toDateValue) {
        this.toDateValue = toDateValue;
    }

    public Double getFromDoubleValue() {
        return fromDoubleValue;
    }

    public void setFromDoubleValue(Double fromDoubleValue) {
        this.fromDoubleValue = fromDoubleValue;
    }

    public Double getToDoubleValue() {
        return toDoubleValue;
    }

    public void setToDoubleValue(Double toDoubleValue) {
        this.toDoubleValue = toDoubleValue;
    }

    /**
     * @return the booleanValue
     */
    public Boolean getBooleanValue() {
        return booleanValue;
    }

    /**
     * @param booleanValue the booleanValue to set
     */
    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    /**
     * Does a price plan value match an corresponding attribute
     * 
     * @param attributesValue Attributes to compare to
     * @return True if value match
     */
    public boolean isMatch(Set<AttributeValue> attributesValue) {
        return attributesValue.stream().anyMatch(attributeValue -> attributeValue.getAttribute().getId().longValue() == attributeId && pricePlanMatrixColumnType.valueMatch(this, attributeValue));
    }
}