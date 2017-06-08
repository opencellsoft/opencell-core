package org.meveo.api.dto.dwh;

import java.math.BigDecimal;

import org.meveo.model.dwh.MeasuredValue;
import org.meveo.model.dwh.MeasurementPeriodEnum;
import org.meveo.model.shared.DateUtils;

/**
 * @author Edward P. Legaspi
 **/
public class MeasuredValueDto {

	private String measurableQuantityCode;
	protected String code;
	private MeasurementPeriodEnum measurementPeriod;
	private String date;
	private String dimension1;
	private String dimension2;
	private String dimension3;
	private String dimension4;
	private BigDecimal value;

	public MeasuredValueDto() {

	}

	public MeasuredValueDto(MeasuredValue e) {
		measurableQuantityCode = e.getMeasurableQuantity().getCode();
		code = e.getCode();
        measurementPeriod = e.getMeasurementPeriod();
        date = e.getDate() != null ? DateUtils.formatDateWithPattern(e.getDate(), "yyyy-MM-dd'T'HH:mm:ss") : null;
        dimension1 = e.getDimension1();
		dimension2 = e.getDimension2();
		dimension3 = e.getDimension3();
		dimension4 = e.getDimension4();
		value      = e.getValue();
	}

	public String getMeasurableQuantityCode() {
		return measurableQuantityCode;
	}

	public void setMeasurableQuantityCode(String measurableQuantityCode) {
		this.measurableQuantityCode = measurableQuantityCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public MeasurementPeriodEnum getMeasurementPeriod() {
		return measurementPeriod;
	}

	public void setMeasurementPeriod(MeasurementPeriodEnum measurementPeriod) {
		this.measurementPeriod = measurementPeriod;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDimension1() {
		return dimension1;
	}

	public void setDimension1(String dimension1) {
		this.dimension1 = dimension1;
	}

	public String getDimension2() {
		return dimension2;
	}

	public void setDimension2(String dimension2) {
		this.dimension2 = dimension2;
	}

	public String getDimension3() {
		return dimension3;
	}

	public void setDimension3(String dimension3) {
		this.dimension3 = dimension3;
	}

	public String getDimension4() {
		return dimension4;
	}

	public void setDimension4(String dimension4) {
		this.dimension4 = dimension4;
	}

	/**
	 * @return the value
	 */
	public BigDecimal getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
