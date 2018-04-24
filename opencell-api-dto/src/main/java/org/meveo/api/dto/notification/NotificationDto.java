package org.meveo.api.dto.notification;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationEventTypeEnum;

/**
 * The Class NotificationDto.
 *
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "Notification")
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3931479706274647165L;

    /** The class name filter. */
    @XmlElement(required = true)
    private String classNameFilter;

    /** Valid values: CREATED, UPDATED, REMOVED, TERMINATED, DISABLED, PROCESSED, REJECTED, REJECTED_CDR, LOGGED_IN, INBOUND_REQ, ENABLED. */
    @XmlElement(required = true)
    private NotificationEventTypeEnum eventTypeFilter;

    /** The el filter. */
    private String elFilter;
    
    /** The script instance code. */
    private String scriptInstanceCode;
    
    /** The script params. */
    private Map<String, String> scriptParams = new HashMap<String, String>();
    
    /** The counter template. */
    private String counterTemplate;
    
    /** The priority. */
    private int priority;
    
    /** The active. */
    private Boolean active = null;

    /**
     * Instantiates a new notification dto.
     */
    public NotificationDto() {

    }

    /**
     * v5.0: add active field
     * 
     * @author akadid abdelmounaim
     * @param notification the Notification entity
     * @lastModifiedVersion 5.0
     */
    public NotificationDto(Notification notification) {
        super(notification);
        classNameFilter = notification.getClassNameFilter();
        eventTypeFilter = notification.getEventTypeFilter();
        elFilter = notification.getElFilter();
        scriptInstanceCode = notification.getScriptInstance() == null ? null : notification.getScriptInstance().getCode();
        if (notification.getCounterTemplate() != null) {
            counterTemplate = notification.getCounterTemplate().getCode();
        }
        if (notification.getParams() != null) {
            scriptParams.putAll(notification.getParams());
        }
        priority = notification.getPriority();
        active = notification.isActive();
        // scriptParams = e.getParams();
    }

    /**
     * Gets the class name filter.
     *
     * @return the class name filter
     */
    public String getClassNameFilter() {
        return classNameFilter;
    }

    /**
     * Sets the class name filter.
     *
     * @param classNameFilter the new class name filter
     */
    public void setClassNameFilter(String classNameFilter) {
        this.classNameFilter = classNameFilter;
    }

    /**
     * Gets the event type filter.
     *
     * @return the event type filter
     */
    public NotificationEventTypeEnum getEventTypeFilter() {
        return eventTypeFilter;
    }

    /**
     * Sets the event type filter.
     *
     * @param eventTypeFilter the new event type filter
     */
    public void setEventTypeFilter(NotificationEventTypeEnum eventTypeFilter) {
        this.eventTypeFilter = eventTypeFilter;
    }

    /**
     * Gets the el filter.
     *
     * @return the el filter
     */
    public String getElFilter() {
        return elFilter;
    }

    /**
     * Sets the el filter.
     *
     * @param elFilter the new el filter
     */
    public void setElFilter(String elFilter) {
        this.elFilter = elFilter;
    }

    /**
     * Gets the script params.
     *
     * @return the scriptParams
     */
    public Map<String, String> getScriptParams() {
        return scriptParams;
    }

    /**
     * Sets the script params.
     *
     * @param scriptParams the scriptParams to set
     */
    public void setScriptParams(Map<String, String> scriptParams) {
        this.scriptParams = scriptParams;
    }

    /**
     * Gets the script instance code.
     *
     * @return the scriptInstanceCode
     */
    public String getScriptInstanceCode() {
        return scriptInstanceCode;
    }

    /**
     * Sets the script instance code.
     *
     * @param scriptInstanceCode the scriptInstanceCode to set
     */
    public void setScriptInstanceCode(String scriptInstanceCode) {
        this.scriptInstanceCode = scriptInstanceCode;
    }

    /**
     * Gets the counter template.
     *
     * @return the counter template
     */
    public String getCounterTemplate() {
        return counterTemplate;
    }

    /**
     * Sets the counter template.
     *
     * @param counterTemplate the new counter template
     */
    public void setCounterTemplate(String counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority.
     *
     * @param priority the new priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Getter for active state.
     *
     * @author akadid abdelmounaim
     * @return isActive
     * @lastModifiedVersion 5.0
     */
    public Boolean isActive() {
        return active;
    }

    /**
     * Setter for active state.
     *
     * @author akadid abdelmounaim
     * @param active active state
     * @lastModifiedVersion 5.0
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "NotificationDto [classNameFilter=" + classNameFilter + ", eventTypeFilter=" + eventTypeFilter + ", elFilter=" + elFilter + ", scriptInstanceCode="
                + scriptInstanceCode + ", scriptParams=" + scriptParams + ", counterTemplate=" + counterTemplate + ", priority=" + priority + "]";
    }
}