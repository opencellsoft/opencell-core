package org.meveo.api.dto.catalog;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.catalog.PricePlanMatrixValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixValueDto extends BaseEntityDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6095766234485840716L;

	private Long ppmValueId;

	@NotNull
    private String ppmColumnCode ;

    private Long longValue;

    private Double doubleValue;

    private String stringValue;

    private Date dateValue;

    private Date fromDateValue;
    private Date toDateValue;

    private Double fromDoubleValue;

    private Double toDoubleValue;

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
}
