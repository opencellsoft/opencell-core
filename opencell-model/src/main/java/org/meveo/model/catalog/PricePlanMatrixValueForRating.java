package org.meveo.model.catalog;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.meveo.model.cpq.AttributeValue;

/**
 * A DTO style class for use in rating that synthetize PricePlanMatrixValue entity with additional data from PricePlanMatrixColumn and PricePlanLine entities.
 * 
 * @author Andrius Karpavicius
 *
 */
public class PricePlanMatrixValueForRating implements Serializable {

    private static final long serialVersionUID = -3841571425197748532L;

    private long attributeId;

    private ColumnTypeEnum pricePlanMatrixColumnType;

    private long pricePlanMatrixLineId;

    private boolean defaultLine;

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

    /**
     * Constructor
     * 
     * @param attributeId An attribute ID, corresponding to a Price plan matrix column definition
     * @param pricePlanMatrixColumnType Price plan matrix column type
     * @param pricePlanMatrixLineId Price plan matrix line ID
     * @param defaultLine Is this a default line
     * @param longValue Price plan matrix value - Long value
     * @param doubleValue Price plan matrix value - Double value
     * @param stringValue Price plan matrix value - String value
     * @param dateValue Price plan matrix value - Date value
     * @param fromDateValue Price plan matrix value - Date range from value
     * @param toDateValue Price plan matrix value - Date range to value
     * @param fromDoubleValue Price plan matrix value - Double range from value
     * @param toDoubleValue Price plan matrix value - Double range to value
     * @param booleanValue Price plan matrix value - boolean value
     */
    public PricePlanMatrixValueForRating(Long attributeId, ColumnTypeEnum pricePlanMatrixColumnType, Long pricePlanMatrixLineId, boolean defaultLine, Long longValue, Double doubleValue, String stringValue,
            Date dateValue, Date fromDateValue, Date toDateValue, Double fromDoubleValue, Double toDoubleValue, Boolean booleanValue) {

        this.attributeId = attributeId;
        this.pricePlanMatrixColumnType = pricePlanMatrixColumnType;
        this.pricePlanMatrixLineId = pricePlanMatrixLineId;
        this.defaultLine = defaultLine;
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

    public long getPricePlanMatrixLineId() {
        return pricePlanMatrixLineId;
    }

    public void setPricePlanMatrixLineId(long pricePlanMatrixLineId) {
        this.pricePlanMatrixLineId = pricePlanMatrixLineId;
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

        if (defaultLine) {
            return true;
        }
        return attributesValue.stream().anyMatch(attributeValue -> attributeValue.getAttribute().getId().longValue() == attributeId && pricePlanMatrixColumnType.valueMatch(this, attributeValue));
    }
}