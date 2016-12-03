package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTemplateLevel;
import org.meveo.model.catalog.CounterTypeEnum;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "CounterTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CounterTemplateDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 2587489734648000805L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    private String description;

    @XmlAttribute(required = true)
    private String calendar;

    private String unity;
    private CounterTypeEnum type;
    private BigDecimal ceiling;
    private boolean disabled;
    private CounterTemplateLevel counterLevel;
    private String ceilingExpressionEl;
    private String notificationLevels;

    public CounterTemplateDto() {
    }

    public CounterTemplateDto(CounterTemplate e) {
        code = e.getCode();
        description = e.getDescription();
        unity = e.getUnityDescription();
        type = e.getCounterType();
        ceiling = e.getCeiling();
        disabled = e.isDisabled();
        calendar = e.getCalendar().getCode();
        counterLevel = e.getCounterLevel();
        ceilingExpressionEl = e.getCeilingExpressionEl();
        notificationLevels = e.getNotificationLevels();
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

    public String getUnity() {
        return unity;
    }

    public void setUnity(String unity) {
        this.unity = unity;
    }

    public CounterTypeEnum getType() {
        return type;
    }

    public void setType(CounterTypeEnum type) {
        this.type = type;
    }

    public BigDecimal getCeiling() {
        return ceiling;
    }

    public void setCeiling(BigDecimal ceiling) {
        this.ceiling = ceiling;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public CounterTemplateLevel getCounterLevel() {
        return counterLevel;
    }

    public void setCounterLevel(CounterTemplateLevel counterLevel) {
        this.counterLevel = counterLevel;
    }

    public String getCeilingExpressionEl() {
        return ceilingExpressionEl;
    }

    public void setCeilingExpressionEl(String ceilingExpressionEl) {
        this.ceilingExpressionEl = ceilingExpressionEl;
    }

    public String getNotificationLevels() {
        return notificationLevels;
    }

    public void setNotificationLevels(String notificationLevels) {
        this.notificationLevels = notificationLevels;
    }

    @Override
    public String toString() {
        return String
            .format(
                "CounterTemplateDto [code=%s, description=%s, calendar=%s, unity=%s, type=%s, ceiling=%s, disabled=%s, counterLevel=%s, ceilingExpressionEl=%s, notificationLevels=%s]",
                code, description, calendar, unity, type, ceiling, disabled, counterLevel, ceilingExpressionEl, notificationLevels);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        } else if (!(obj instanceof CounterTemplateDto)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        CounterTemplateDto other = (CounterTemplateDto) obj;

        if (getCode() == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!getCode().equals(other.getCode())) {
            return false;
        }
        return true;
    }
}