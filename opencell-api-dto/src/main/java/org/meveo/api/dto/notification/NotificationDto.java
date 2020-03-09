/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.notification;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
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
public abstract class NotificationDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3931479706274647165L;

    /** The class name filter. */
    @XmlElement(required = true)
    private String classNameFilter;

    /**
     * Valid values: CREATED, UPDATED, REMOVED, TERMINATED, DISABLED, PROCESSED, REJECTED, REJECTED_CDR, LOGGED_IN, INBOUND_REQ, ENABLED
     */
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

    /**
     * Is Notification active. A negative of Disabled. Deprecated in 5.0.1. Use Disabled field instead.
     * 
     */
    @Deprecated
    private Boolean active = null;

    /** 
     * Whether this notification will be run in async mode.
     */
    private Boolean runAsync = false;
    

    /**
     * Whether this notification will be persisted when successful.
     */
    private Boolean saveSuccessfulNotifications = true;

    /**
     * Instantiates a new notification dto.
     */
    public NotificationDto() {

    }

    /**
     * Convert Notifictaion entity to DTO
     * 
     * @param notification Entity to convert
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
        runAsync = notification.isRunAsync();
        saveSuccessfulNotifications = notification.isSaveSuccessfulNotifications();
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
     * 
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

    /**
     * Gets boolean value of whether this notification will be run in async mode.
     * @return true / false
     */
    public Boolean isRunAsync() {
        return runAsync;
    }

    /**
     * Sets boolean value of whether this notification will be run in async mode.
     * @return true / false
     */
    public void setRunAsync(Boolean runAsync) {
        this.runAsync = runAsync;
    }
    
    /**
     * Gets boolean value of whether this notification will be persisted when successful.
     * @return true / false
     */
    public Boolean iSSaveSuccessfulNotifications() {
        return saveSuccessfulNotifications;
    }


    /**
     * Set boolean value of whether this notification will be persisted when successful.
     */
    public void setSaveSuccessfulNotifications(Boolean saveSuccessfulNotifications) {
        this.saveSuccessfulNotifications = saveSuccessfulNotifications;
    }
    
    
}