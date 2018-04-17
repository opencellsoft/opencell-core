package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.CounterTemplateLevel;
import org.meveo.model.catalog.CounterTypeEnum;

/**
 * The Class CounterTemplateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "CounterTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class CounterTemplateDto extends BusinessDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2587489734648000805L;

    /** The calendar. */
    @XmlAttribute(required = true)
    private String calendar;

    /** The unity. */
    private String unity;

    /** The type. */
    private CounterTypeEnum type;

    /** The ceiling. */
    private BigDecimal ceiling;

    /** The disabled. */
    private boolean disabled;

    /** The counter level. */
    private CounterTemplateLevel counterLevel;

    /** The ceiling expression el. */
    private String ceilingExpressionEl;

    /** The notification levels. */
    private String notificationLevels;

    /**
     * Instantiates a new counter template dto.
     */
    public CounterTemplateDto() {
    }

    /**
     * Instantiates a new counter template dto.
     *
     * @param counterTemplate the CounterTemplate entity
     */
    public CounterTemplateDto(CounterTemplate counterTemplate) {
        super(counterTemplate);
        unity = counterTemplate.getUnityDescription();
        type = counterTemplate.getCounterType();
        ceiling = counterTemplate.getCeiling();
        disabled = counterTemplate.isDisabled();
        calendar = counterTemplate.getCalendar().getCode();
        counterLevel = counterTemplate.getCounterLevel();
        ceilingExpressionEl = counterTemplate.getCeilingExpressionEl();
        notificationLevels = counterTemplate.getNotificationLevels();
    }

    /**
     * Gets the unity.
     *
     * @return the unity
     */
    public String getUnity() {
        return unity;
    }

    /**
     * Sets the unity.
     *
     * @param unity the new unity
     */
    public void setUnity(String unity) {
        this.unity = unity;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public CounterTypeEnum getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(CounterTypeEnum type) {
        this.type = type;
    }

    /**
     * Gets the ceiling.
     *
     * @return the ceiling
     */
    public BigDecimal getCeiling() {
        return ceiling;
    }

    /**
     * Sets the ceiling.
     *
     * @param ceiling the new ceiling
     */
    public void setCeiling(BigDecimal ceiling) {
        this.ceiling = ceiling;
    }

    /**
     * Checks if is disabled.
     *
     * @return true, if is disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the disabled.
     *
     * @param disabled the new disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Gets the calendar.
     *
     * @return the calendar
     */
    public String getCalendar() {
        return calendar;
    }

    /**
     * Sets the calendar.
     *
     * @param calendar the new calendar
     */
    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    /**
     * Gets the counter level.
     *
     * @return the counter level
     */
    public CounterTemplateLevel getCounterLevel() {
        return counterLevel;
    }

    /**
     * Sets the counter level.
     *
     * @param counterLevel the new counter level
     */
    public void setCounterLevel(CounterTemplateLevel counterLevel) {
        this.counterLevel = counterLevel;
    }

    /**
     * Gets the ceiling expression el.
     *
     * @return the ceiling expression el
     */
    public String getCeilingExpressionEl() {
        return ceilingExpressionEl;
    }

    /**
     * Sets the ceiling expression el.
     *
     * @param ceilingExpressionEl the new ceiling expression el
     */
    public void setCeilingExpressionEl(String ceilingExpressionEl) {
        this.ceilingExpressionEl = ceilingExpressionEl;
    }

    /**
     * Gets the notification levels.
     *
     * @return the notification levels
     */
    public String getNotificationLevels() {
        return notificationLevels;
    }

    /**
     * Sets the notification levels.
     *
     * @param notificationLevels the new notification levels
     */
    public void setNotificationLevels(String notificationLevels) {
        this.notificationLevels = notificationLevels;
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
    
    @Override
    public String toString() {
        return String.format(
            "CounterTemplateDto [code=%s, description=%s, calendar=%s, unity=%s, type=%s, ceiling=%s, disabled=%s, counterLevel=%s, ceilingExpressionEl=%s, notificationLevels=%s]",
            getCode(), getDescription(), calendar, unity, type, ceiling, disabled, counterLevel, ceilingExpressionEl, notificationLevels);
    }    
}