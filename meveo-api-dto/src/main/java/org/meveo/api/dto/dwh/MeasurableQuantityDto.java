package org.meveo.api.dto.dwh;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.BaseDto;
import org.meveo.model.dwh.MeasurableQuantity;
import org.meveo.model.dwh.MeasurementPeriodEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "MeasurableQuantity")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeasurableQuantityDto extends BaseDto {

    private static final long serialVersionUID = 2678416518718451635L;

    @XmlAttribute(required = true)
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
    private MeasurementPeriodEnum measurementPeriod;
    private Date lastMeasureDate;

    public boolean isCodeOnly() {
        return StringUtils.isBlank(description) && StringUtils.isBlank(theme) && StringUtils.isBlank(dimension1) && StringUtils.isBlank(dimension2)
                && StringUtils.isBlank(dimension3) && StringUtils.isBlank(dimension4) && StringUtils.isBlank(sqlQuery) && measurementPeriod == null && lastMeasureDate == null;
    }

    public MeasurableQuantityDto() {
        super();
    }

    public MeasurableQuantityDto(MeasurableQuantity mq) {
        super();
        setCode(mq.getCode());
        setDescription(mq.getDescription());
        setTheme(mq.getTheme());
        setDimension1(mq.getDimension1());
        setDimension2(mq.getDimension2());
        setDimension3(mq.getDimension3());
        setDimension4(mq.getDimension4());
        setEditable(mq.isEditable());
        setAdditive(mq.isAdditive());
        setSqlQuery(mq.getSqlQuery());
        setMeasurementPeriod(mq.getMeasurementPeriod());
        setLastMeasureDate(mq.getLastMeasureDate());
    }

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

    @Override
    public String toString() {
        return String
            .format(
                "MeasurableQuantityDto [code=%s, description=%s, theme=%s, dimension1=%s, dimension2=%s, dimension3=%s, dimension4=%s, editable=%s, additive=%s, sqlQuery=%s, measurementPeriod=%s, lastMeasureDate=%s]",
                code, description, theme, dimension1, dimension2, dimension3, dimension4, editable, additive, sqlQuery, measurementPeriod, lastMeasureDate);
    }
}