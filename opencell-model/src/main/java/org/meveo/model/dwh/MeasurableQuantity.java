package org.meveo.model.dwh;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

@Entity
@ModuleItem
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "dwh_measurable_quant", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dwh_measurable_quant_seq"), })
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasurableQuantity extends EnableBusinessEntity {

    private static final long serialVersionUID = -4864192159320969937L;

    @Column(name = "theme", length = 255)
    @Size(max = 255)
    private String theme;

    @Column(name = "dimension_1", length = 255)
    @Size(max = 255)
    private String dimension1;

    @Column(name = "dimension_2", length = 255)
    @Size(max = 255)
    private String dimension2;

    @Column(name = "dimension_3", length = 255)
    @Size(max = 255)
    private String dimension3;

    @Column(name = "dimension_4", length = 255)
    @Size(max = 255)
    private String dimension4;

    @Type(type = "numeric_boolean")
    @Column(name = "editable")
    private boolean editable;

    @Type(type = "numeric_boolean")
    @Column(name = "additive")
    private boolean additive;
    /**
     * expect to return a list of (Date measureDate, Long value) that will be used to create measuredValue.
     */
    @Column(name = "sql_query", length = 2000)
    @Size(max = 2000)
    private String sqlQuery;

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_period")
    private MeasurementPeriodEnum measurementPeriod;

    @Column(name = "last_measure_date")
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

    public Date getPreviousDate(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        switch (measurementPeriod) {
        case DAILY:
            calendar.add(java.util.Calendar.DAY_OF_MONTH, -1);
            break;
        case WEEKLY:
            calendar.add(java.util.Calendar.WEEK_OF_YEAR, -1);
            break;
        case MONTHLY:
            calendar.add(java.util.Calendar.MONTH, -1);
            break;
        case YEARLY:
            calendar.add(java.util.Calendar.YEAR, -1);
            break;
        }
        return calendar.getTime();
    }

    public Date getNextMeasureDate() {
        GregorianCalendar calendar = new GregorianCalendar();
        Date result = new Date();
        if (lastMeasureDate != null) {
            calendar.setTime(lastMeasureDate);
            switch (measurementPeriod) {
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
            result = calendar.getTime();
        }
        return result;
    }

    public void increaseMeasureDate() {
        if (lastMeasureDate == null) {
            lastMeasureDate = new Date();
        } else {
            lastMeasureDate = getNextMeasureDate();
        }
    }

    public boolean isAdditive() {
        return additive;
    }

    public void setAdditive(boolean additive) {
        this.additive = additive;
    }

}
