package org.meveo.api.dto.generic.wf;

import static java.util.Optional.ofNullable;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.generic.wf.Action;
import org.meveo.model.notification.Notification;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Arrays;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(FIELD)
public class GWFActionDto extends BaseEntityDto {

    private String description;

    private String uuid;

    private int priority;

    private ActionTypesEnum type;

    private String conditionEl;

    @XmlElement(name = "isAsynchronous")
    private boolean asynchronous;

    private String actionScriptCode;

    private String notificationCode;

    private String valueEl;

    private LogLevel logLevel;

    private String field;

    private Map<String, String> parameters;

    public GWFActionDto() {
    }

    public GWFActionDto(Action action) {
        this.setUuid(action.getUuid());
        this.setDescription(action.getDescription());
        this.setConditionEl(action.getConditionEl());
        this.setPriority(action.getPriority());
        this.setAsynchronous(action.isAsynchronous());
        ofNullable(action.getActionScript()).ifPresent(script -> this.setActionScriptCode(script.getCode()));
        if(action.getNotification() != null) {
            Notification notification = action.getNotification();
            this.setNotificationCode(notification.getCode());
            this.setParameters(notification.getParams());
        }
        this.setType(ActionTypesEnum.valueOf(action.getType()));
        this.setValueEl(action.getValueEL());
        this.setField(action.getFieldToUpdate());
        ofNullable(action.getLogLevel()).ifPresent(level -> this.setLogLevel(LogLevel.fromValue(level)));
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getConditionEl() {
        return conditionEl;
    }

    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    public boolean isAsynchronous() {
        return this.asynchronous;
    }

    public void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    public String getActionScriptCode() {
        return actionScriptCode;
    }

    public void setActionScriptCode(String actionScriptCode) {
        this.actionScriptCode = actionScriptCode;
    }

    public String getNotificationCode() {
        return notificationCode;
    }

    public void setNotificationCode(String notificationCode) {
        this.notificationCode = notificationCode;
    }

    public String getValueEl() {
        return valueEl;
    }

    public void setValueEl(String valueEl) {
        this.valueEl = valueEl;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public ActionTypesEnum getType() {
        return type;
    }

    public void setType(ActionTypesEnum type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public enum LogLevel {
        INFO, DEBUG, TRACE;

        public static LogLevel fromValue(String name) {
            return Arrays.stream(values())
                    .filter(log -> log.name().equalsIgnoreCase(name))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
        }
    }
}