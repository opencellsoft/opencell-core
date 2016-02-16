package org.meveo.api.dto.dwh;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "MeasurableQuantity")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasurableQuantityDto extends BaseDto {

	private static final long serialVersionUID = 2678416518718451635L;

	@XmlAttribute
	private String code;

	@XmlAttribute
	private String description;

	private String theme;
	private String dimension1;
	private String dimension2;
	private String dimension3;
	private String dimension4;
	private boolean editable;
	private boolean additive;
	private String sqlQuery;
	private String measurementPeriod;
	private Date lastMeasureDate;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
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

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isAdditive() {
		return additive;
	}

	public void setAdditive(boolean additive) {
		this.additive = additive;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getMeasurementPeriod() {
		return measurementPeriod;
	}

	public void setMeasurementPeriod(String measurementPeriod) {
		this.measurementPeriod = measurementPeriod;
	}

	public Date getLastMeasureDate() {
		return lastMeasureDate;
	}

	public void setLastMeasureDate(Date lastMeasureDate) {
		this.lastMeasureDate = lastMeasureDate;
	}

	@Override
	public String toString() {
		return "MeasurableQuantityDto [code=" + code + ", description=" + description + ", theme=" + theme
				+ ", dimension1=" + dimension1 + ", dimension2=" + dimension2 + ", dimension3=" + dimension3
				+ ", dimension4=" + dimension4 + ", editable=" + editable + ", additive=" + additive + ", sqlQuery="
				+ sqlQuery + ", measurementPeriod=" + measurementPeriod + ", lastMeasureDate=" + lastMeasureDate + "]";
	}

}
