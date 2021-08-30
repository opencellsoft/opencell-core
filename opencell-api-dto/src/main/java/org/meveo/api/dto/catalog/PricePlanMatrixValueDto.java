package org.meveo.api.dto.catalog;

import java.util.Date;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.catalog.PricePlanMatrixValue;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixValueDto extends BaseEntityDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6095766234485840716L;

    @Schema(description = "The price plan matrix value id")
	private Long ppmValueId;

	@NotNull
    @Schema(description = "The price plan column code", required = true)
    private String ppmColumnCode ;

    @Schema(description = "The long value")
    private Long longValue;

    @Schema(description = "The double value")
    private Double doubleValue;

    @Schema(description = "The string value")
    private String stringValue;

    @Schema(description = "The date value")
    private Date dateValue;

    @Schema(description = "The from date value")
    private Date fromDateValue;
    
    @Schema(description = "The to date value")
    private Date toDateValue;

    @Schema(description = "The from double value")
    private Double fromDoubleValue;

    @Schema(description = "The to double value")
    private Double toDoubleValue;

    @Schema(description = "The boolean value")
    private Boolean booleanValue;

	public PricePlanMatrixValueDto() {
	}

	public PricePlanMatrixValueDto(PricePlanMatrixValue value) {
		ppmValueId = value.getId();
		ppmColumnCode = value.getPricePlanMatrixColumn().getCode();
		longValue = value.getLongValue();
		doubleValue = value.getDoubleValue();
		stringValue = value.getStringValue();
		dateValue = value.getDateValue();
		fromDateValue = value.getFromDateValue();
		toDateValue = value.getToDateValue();
		fromDoubleValue = value.getToDoubleValue();
		toDateValue = value.getToDateValue();
		booleanValue = value.getBooleanValue();
	}

	/**
	 * @return the ppmColumnCode
	 */
	public String getPpmColumnCode() {
		return ppmColumnCode;
	}

	/**
	 * @param ppmColumnCode the ppmColumnCode to set
	 */
	public void setPpmColumnCode(String ppmColumnCode) {
		this.ppmColumnCode = ppmColumnCode;
	}

	/**
	 * @return the longValue
	 */
	public Long getLongValue() {
		return longValue;
	}

	/**
	 * @param longValue the longValue to set
	 */
	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	/**
	 * @return the doubleValue
	 */
	public Double getDoubleValue() {
		return doubleValue;
	}

	/**
	 * @param doubleValue the doubleValue to set
	 */
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	/**
	 * @return the stringValue
	 */
	public String getStringValue() {
		return stringValue;
	}

	/**
	 * @param stringValue the stringValue to set
	 */
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	/**
	 * @return the dateValue
	 */
	public Date getDateValue() {
		return dateValue;
	}

	/**
	 * @param dateValue the dateValue to set
	 */
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	/**
	 * @return the fromDateValue
	 */
	public Date getFromDateValue() {
		return fromDateValue;
	}

	/**
	 * @param fromDateValue the fromDateValue to set
	 */
	public void setFromDateValue(Date fromDateValue) {
		this.fromDateValue = fromDateValue;
	}

	/**
	 * @return the toDateValue
	 */
	public Date getToDateValue() {
		return toDateValue;
	}

	/**
	 * @param toDateValue the toDateValue to set
	 */
	public void setToDateValue(Date toDateValue) {
		this.toDateValue = toDateValue;
	}

	/**
	 * @return the fromDoubleValue
	 */
	public Double getFromDoubleValue() {
		return fromDoubleValue;
	}

	/**
	 * @param fromDoubleValue the fromDoubleValue to set
	 */
	public void setFromDoubleValue(Double fromDoubleValue) {
		this.fromDoubleValue = fromDoubleValue;
	}

	/**
	 * @return the toDoubleValue
	 */
	public Double getToDoubleValue() {
		return toDoubleValue;
	}

	/**
	 * @param toDoubleValue the toDoubleValue to set
	 */
	public void setToDoubleValue(Double toDoubleValue) {
		this.toDoubleValue = toDoubleValue;
	}

	public Long getPpmValueId() {
		return ppmValueId;
	}

	public void setPpmValueId(Long ppmValueId) {
		this.ppmValueId = ppmValueId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(dateValue, doubleValue, fromDateValue, fromDoubleValue, longValue, ppmColumnCode,
				ppmValueId, stringValue, toDateValue, toDoubleValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PricePlanMatrixValueDto other = (PricePlanMatrixValueDto) obj;
		return Objects.equals(dateValue, other.dateValue)
				&& Objects.equals(fromDateValue, other.fromDateValue)
				&& Objects.equals(ppmColumnCode, other.ppmColumnCode) 
				&& Objects.equals(ppmValueId, other.ppmValueId)
				&& Objects.equals(stringValue, other.stringValue) 
				&& Objects.equals(toDateValue, other.toDateValue)
				&& checkToDoubleValue(fromDoubleValue, other.fromDoubleValue)
				&& checkToDoubleValue(toDoubleValue, other.toDoubleValue)
				&& checkToDoubleValue(doubleValue, other.doubleValue)
				&& checkoutToLongValue(longValue, other.longValue);
	}
	
	public boolean checkToDoubleValue(Double numeric, Double toCompare) {
		if(numeric == null && toCompare == null)
			return true;
		if(numeric != null && toCompare == null)
			return false;
		if(numeric == null && toCompare != null)
			return false;
		double epsilon = 0.000001d;
		return Math.abs(numeric - toCompare) < epsilon;
	}
	
	public boolean checkoutToLongValue(Long numeric, Long toCompare) {
		if(numeric == null && toCompare == null)
			return true;
		if(numeric != null && toCompare == null)
			return false;
		if(numeric == null && toCompare != null)
			return false;
		return numeric.equals(toCompare);
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
	
	
}
