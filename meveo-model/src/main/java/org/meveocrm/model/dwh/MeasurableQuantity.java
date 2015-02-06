package org.meveocrm.model.dwh;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "DWH_MEASURABLE_QUANT", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "DWH_MEASURABLE_QUANT_SEQ")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasurableQuantity extends BusinessEntity {

	private static final long serialVersionUID = -4864192159320969937L;

	@Column(name = "THEME", length = 255)
	private String theme;

	@Column(name = "DIMENSION_1", length = 255)
	private String dimension1;

	@Column(name = "DIMENSION_2", length = 255)
	private String dimension2;

	@Column(name = "DIMENSION_3", length = 255)
	private String dimension3;

	@Column(name = "DIMENSION_4", length = 255)
	private String dimension4;

	@Column(name = "EDITABLE", length = 255)
	private boolean editable;
	
	@Column(name = "LAST_EXECUTION")
	@Temporal(TemporalType.DATE)
	private Date lastExecutionDate;

	/**
	 * expect to return a list of (Date measureDate, Long value) that will be
	 * used to create measuredValue. be careful that super admin MUST validate
	 * those queries as they could break separation of data between providers
	 */
	@Column(name = "SQL_QUERY", length = 2000)
	private String sqlQuery;

	@Enumerated(EnumType.STRING)
	@Column(name = "MEASUREMENT_PERIOD")
	private MeasurementPeriodEnum measurementPeriod;
	
	@Column(name = "LAST_MEASURE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastMeasureDate;

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

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public MeasurementPeriodEnum getMeasurementPeriod() {
		return measurementPeriod;
	}

	public void setMeasurementPeriod(MeasurementPeriodEnum measurementPeriod) {
		this.measurementPeriod = measurementPeriod;
	}

	public Date getLastMeasureDate() {
		return lastMeasureDate;
	}

	public void setLastMeasureDate(Date lastMeasureDate) {
		this.lastMeasureDate = lastMeasureDate;
	}

	public Date getLastExecutionDate() {
		return lastExecutionDate;
	}

	public void setLastExecutionDate(Date lastExecutionDate) {
		this.lastExecutionDate = lastExecutionDate;
	}
	
	public Date getNextExcutionDate(){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(lastExecutionDate);
        switch(measurementPeriod){
        case DAILY:
        	calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        	break;
        case WEEKLY:
        	calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1);
        	break;
        case MONTHLY:
        	calendar.add(java.util.Calendar.MONTH, 1);
        	break;
        case YEARLY:
        	calendar.add(java.util.Calendar.YEAR, 1);
        	break;
        }
        return calendar.getTime();
	}
	
	public void increaseExecutionDate(){
		
	}
	
}
